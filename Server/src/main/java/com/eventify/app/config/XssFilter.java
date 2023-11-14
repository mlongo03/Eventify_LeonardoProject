package com.eventify.app.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import java.util.Collections;
import java.util.List;

@Component
public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (containsMaliciousJavaScript(request)) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean containsMaliciousJavaScript(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        List<String> paramNameList = Collections.list(paramNames);

        for (String paramName : paramNameList) {
            String paramValue = request.getParameter(paramName);
            if (paramValue != null) {

                if (paramValue.contains("<script>") || paramValue.contains("alert(")) {
                    return true;
                }
                String safeParamValue = HtmlUtils.htmlEscape(paramValue);

                if (!safeParamValue.equals(paramValue)) {
                    return true;
                }
            }
        }
        return false;
    }
}
