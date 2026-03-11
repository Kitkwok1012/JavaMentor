package com.javamentor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Filter - Caffeine-based rate limiting
 * Prevents memory leaks from unbounded ConcurrentHashMap
 */
@Component
public class RateLimitFilter implements Filter {

    @Value("${ratelimit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    private final ObjectMapper objectMapper;

    // Caffeine cache with TTL and max size to prevent memory leaks
    private final Cache<String, RateLimitInfo> ipTracker = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(50_000)
            .build();

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
        
        // Get or create rate limit info - Caffeine handles expiration automatically
        RateLimitInfo info = ipTracker.get(key, k -> new RateLimitInfo());

        int currentCount = info.count.incrementAndGet();

        if (currentCount > requestsPerMinute) {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            Map<String, Object> error = Map.of(
                "error", "Rate limit exceeded",
                "message", "Too many requests. Please try again later.",
                "retryAfter", 60
            );
            
            objectMapper.writeValue(httpResponse.getWriter(), error);
            return;
        }

        // Add rate limit headers
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        httpResponse.setHeader("X-RateLimit-Remaining", 
            String.valueOf(requestsPerMinute - currentCount));

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Rate limit info holder - simple counter
     */
    private static class RateLimitInfo {
        AtomicInteger count = new AtomicInteger(0);
    }
}
