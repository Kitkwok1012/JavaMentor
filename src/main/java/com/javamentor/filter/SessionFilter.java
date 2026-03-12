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

        // Check if user already has session cookie
        String sessionId = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AppConstants.SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        // If no session cookie, create new one
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(AppConstants.SESSION_COOKIE_NAME, sessionId);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
            httpResponse.addCookie(cookie);
        }

        // Set session ID as request attribute for controllers to use
        httpRequest.setAttribute(SESSION_ATTRIBUTE, sessionId);

        chain.doFilter(request, response);
    }
}
