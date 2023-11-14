package com.eventify.app.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RequestLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_SECOND = 30;
    private Map<String, Integer> requestCountMap = new HashMap<>();
    private Map<String, Long> requestTimestampMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String clientIP = request.getRemoteAddr();

        if (isRequestLimitExceeded(clientIP, (HttpServletResponse) response)) {
            return;
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isRequestLimitExceeded(String clientIP, HttpServletResponse httpResponse) {
        try {
            long currentTime = System.currentTimeMillis();

            if (requestCountMap.containsKey(clientIP)) {
                int count = requestCountMap.get(clientIP);
                long lastRequestTime = requestTimestampMap.get(clientIP);
                long timeElapsed = currentTime - lastRequestTime;

                if (count >= MAX_REQUESTS_PER_SECOND && timeElapsed < 1000) {
                    httpResponse.setStatus(429);
                    httpResponse.getWriter().write("Limite di richieste al secondo superato.");
                    return true;
                } else if (timeElapsed >= 1000) {
                    requestCountMap.put(clientIP, 1);
                    requestTimestampMap.put(clientIP, currentTime);
                } else {
                    requestCountMap.put(clientIP, count + 1);
                }
            } else {
                requestCountMap.put(clientIP, 1);
                requestTimestampMap.put(clientIP, currentTime);
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
