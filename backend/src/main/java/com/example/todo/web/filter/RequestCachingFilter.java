package com.example.todo.web.filter;

import com.example.todo.domain.model.LogPayload;
import com.example.todo.infrastructure.repository.LogRepository;
import com.example.todo.web.exception.ApiExceptionHandler;
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
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestCachingFilter extends OncePerRequestFilter {

    private final LogRepository logRepository;
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

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("method", wrappedRequest.getMethod());
            requestPayload.put("uri", wrappedRequest.getRequestURI());
            requestPayload.put("body", requestBody);

            LogPayload payload = LogPayload.builder()
                .request(requestPayload)
                .response(responseBody)
                .status(status)
                .durationMs(durationMs)
                .build();

            String label = wrappedRequest.getMethod() + " " + wrappedRequest.getRequestURI();

            Exception ex = (Exception) wrappedRequest.getAttribute(ApiExceptionHandler.EXCEPTION_ATTRIBUTE);
            if (status >= 400 || ex != null) {
                logRepository.error(label, payload, ex);
            } else {
                logRepository.info(label, payload);
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
            return new String(content, java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
