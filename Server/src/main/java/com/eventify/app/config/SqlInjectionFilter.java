package com.eventify.app.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SqlInjectionFilter extends OncePerRequestFilter {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*[;]+.*|.*(--)+.*|.*\\b(ALTER|CREATE|DELETE|DROP|EXEC|INSERT|MERGE|SELECT|UPDATE)\\b.*",
            Pattern.CASE_INSENSITIVE);

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlInjectionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Map<String, String[]> parameters = request.getParameterMap();

        for (String parameterName : parameters.keySet()) {
            String[] parameterValues = parameters.get(parameterName);
            for (String parameterValue : parameterValues) {

                Matcher matcher = SQL_INJECTION_PATTERN.matcher(parameterValue);
                if (matcher.matches()) {

                    LOGGER.info("SQL injection attack detected! Request: {}", request);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("SQL injection attack detected!");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
