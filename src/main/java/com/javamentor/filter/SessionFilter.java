package com.javamentor.filter;

import com.javamentor.config.AppConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class SessionFilter implements Filter {

    public static final String SESSION_ATTRIBUTE = "userSessionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if user already has session cookie (custom session or JSESSIONID)
        String sessionId = getExistingSessionId(httpRequest);

        // If no session cookie, create new one
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(AppConstants.SESSION_COOKIE_NAME, sessionId);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(AppConstants.SESSION_COOKIE_MAX_AGE_SECONDS);
            // Set SameSite for modern browsers
            cookie.setAttribute("SameSite", "Lax");
            httpResponse.addCookie(cookie);
        }

        // Set session ID as request attribute for controllers to use
        httpRequest.setAttribute(SESSION_ATTRIBUTE, sessionId);

        chain.doFilter(request, response);
    }

    /**
     * Get existing session ID from cookies (custom or JSESSIONID)
     */
    private String getExistingSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AppConstants.SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            // Fallback: check for JSESSIONID (standard Servlet session)
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
