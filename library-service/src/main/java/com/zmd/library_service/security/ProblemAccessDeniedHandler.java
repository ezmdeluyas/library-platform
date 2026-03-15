package com.zmd.library_service.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

import static com.zmd.library_service.api.error.ProblemFields.TIMESTAMP;
import static com.zmd.library_service.api.error.ProblemTypes.ACCESS_DENIED;
import static com.zmd.library_service.api.error.ProblemUris.BASE;

@Component
@RequiredArgsConstructor
public class ProblemAccessDeniedHandler implements AccessDeniedHandler {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource."
        );
        pd.setTitle("Access denied");
        pd.setType(URI.create(BASE + ACCESS_DENIED));

        String instance = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        pd.setInstance(URI.create(instance));
        pd.setProperty(TIMESTAMP, OffsetDateTime.now());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(PROBLEM_JSON.toString());

        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}