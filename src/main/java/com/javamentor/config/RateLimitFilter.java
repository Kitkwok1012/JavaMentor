package com.javamentor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Filter - Simple IP-based rate limiting
 * Prevents API abuse and scraping
 */
@Component
public class RateLimitFilter implements Filter {

    @Value("${ratelimit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    private final Map<String, RateLimitInfo> ipTracker = new ConcurrentHashMap<>();
    private static final long WINDOW_MS = 60_000; // 1 minute

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!rateLimitEnabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);
        String endpoint = httpRequest.getRequestURI();

        // Skip rate limit for static resources and health checks
        if (endpoint.startsWith("/css") || endpoint.startsWith("/js") || 
            endpoint.startsWith("/images") || endpoint.equals("/") ||
            endpoint.equals("/health")) {
            chain.doFilter(request, response);
            return;
        }

        String key = clientIp + ":" + endpoint;
        RateLimitInfo info = ipTracker.computeIfAbsent(key, k -> new RateLimitInfo());

        long now = System.currentTimeMillis();
        
        // Reset window if expired
        if (now - info.windowStart > WINDOW_MS) {
            info.windowStart = now;
            info.count.set(0);
        }

        if (info.count.incrementAndGet() > requestsPerMinute) {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            Map<String, Object> error = Map.of(
                "error", "Rate limit exceeded",
                "message", "Too many requests. Please try again later.",
                "retryAfter", (WINDOW_MS - (now - info.windowStart)) / 1000
            );
            
            new ObjectMapper().writeValue(httpResponse.getWriter(), error);
            return;
        }

        // Add rate limit headers
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        httpResponse.setHeader("X-RateLimit-Remaining", 
            String.valueOf(requestsPerMinute - info.count.get()));

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitInfo {
        long windowStart = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
    }
}
