package com.zmd.library_service.security;

import com.zmd.library_service.api.error.ProblemTypes;
import com.zmd.library_service.api.error.ProblemUris;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

import static com.zmd.library_service.api.error.ProblemFields.TIMESTAMP;
import static com.zmd.library_service.api.error.ProblemTypes.UNAUTHORIZED;
import static com.zmd.library_service.api.error.ProblemUris.BASE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProblemAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException {

        String detail = resolveDetail(authException);
        String type = String.valueOf(URI.create(ProblemUris.BASE + ProblemTypes.UNAUTHORIZED));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, detail);
        pd.setTitle("Unauthorized");
        pd.setType(URI.create(BASE + UNAUTHORIZED));

        String instance = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        pd.setInstance(URI.create(instance));
        pd.setProperty(TIMESTAMP, OffsetDateTime.now());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(PROBLEM_JSON.toString());

        log.warn("event=authentication_failed status=401 type={} instance={} detail={}", type, instance, detail);

        objectMapper.writeValue(response.getOutputStream(), pd);
    }

    private String resolveDetail(AuthenticationException ex) {
        if (ex == null) {
            return "Authentication is required to access this resource.";
        }

        if (ex instanceof InvalidBearerTokenException) {
            String message = ex.getMessage();
            if (message != null && message.toLowerCase().contains("expired")) {
                return "Bearer token has expired.";
            }
            return "Bearer token is invalid.";
        }

        String message = ex.getMessage();
        if (message != null) {
            String lower = message.toLowerCase();
            if (lower.contains("expired")) {
                return "Bearer token has expired.";
            }
            if (lower.contains("invalid")) {
                return "Bearer token is invalid.";
            }
            if (lower.contains("malformed")) {
                return "Bearer token is malformed.";
            }
        }

        return "Authentication is required to access this resource.";
    }
}