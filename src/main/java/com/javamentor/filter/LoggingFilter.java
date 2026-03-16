package com.javamentor.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Logging Filter - Adds MDC context for structured logging
 * 
 * Adds to MDC:
 * - sessionId: from cookie or generated
 * - requestUri: the request path
 * - userAgent: client user agent
 */
@Component
@Order(1)
public class LoggingFilter implements Filter {

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String USER_AGENT_KEY = "userAgent";
    private static final String QUESTION_ID_KEY = "questionId";
    private static final String TOPIC_ID_KEY = "topicId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Add request metadata to MDC
            MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());
            MDC.put(USER_AGENT_KEY, httpRequest.getHeader("User-Agent"));
            
            // Generate or get session ID for logging
            String sessionId = httpRequest.getSession(true).getId();
            MDC.put(SESSION_ID_KEY, sessionId);
            
            // Add question/topic ID if present in request
            String questionId = httpRequest.getParameter("questionId");
            if (questionId != null) {
                MDC.put(QUESTION_ID_KEY, questionId);
            }
            
            String topicId = httpRequest.getParameter("topicId");
            if (topicId != null) {
                MDC.put(TOPIC_ID_KEY, topicId);
            }
            
            // Add custom header for question/topic ID
            String questionIdHeader = httpRequest.getHeader("X-Question-Id");
            if (questionIdHeader != null) {
                MDC.put(QUESTION_ID_KEY, questionIdHeader);
            }
            
            String topicIdHeader = httpRequest.getHeader("X-Topic-Id");
            if (topicIdHeader != null) {
                MDC.put(TOPIC_ID_KEY, topicIdHeader);
            }
            
            chain.doFilter(request, response);
            
        } finally {
            // Clear MDC after request
            MDC.remove(SESSION_ID_KEY);
            MDC.remove(REQUEST_URI_KEY);
            MDC.remove(USER_AGENT_KEY);
            MDC.remove(QUESTION_ID_KEY);
            MDC.remove(TOPIC_ID_KEY);
        }
    }
}
