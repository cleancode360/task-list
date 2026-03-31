package com.example.todo.shared.log.framework.filter;

import com.example.todo.shared.log.domain.entity.LogPayload;
import com.example.todo.shared.log.domain.gateway.LogGateway;
import com.example.todo.shared.exception.framework.advice.SharedExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final LogGateway logRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper req
            ? req
            : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startMs = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - startMs;
            int status = wrappedResponse.getStatus();

            Object requestBody = parseJson(wrappedRequest.getContentAsByteArray());
            Object responseBody = parseJson(wrappedResponse.getContentAsByteArray());

            LogPayload payload = LogPayload.builder()
                .request(requestBody)
                .response(responseBody)
                .status(status)
                .durationMs(durationMs)
                .build();

            StringBuilder label = new StringBuilder(wrappedRequest.getMethod())
                .append(" ")
                .append(wrappedRequest.getRequestURI());

            String queryString = wrappedRequest.getQueryString();
            if (queryString != null) {
                label.append("?").append(queryString);
            }

            Exception ex = (Exception) wrappedRequest.getAttribute(SharedExceptionHandler.EXCEPTION_ATTRIBUTE);
            if (status >= 400 || ex != null) {
                logRepository.error(label.toString(), payload, ex);
            } else {
                logRepository.info(label.toString(), payload);
            }

            wrappedResponse.copyBodyToResponse();
        }
    }

    private Object parseJson(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(content, Object.class);
        } catch (Exception e) {
            return new String(content, StandardCharsets.UTF_8);
        }
    }
}
