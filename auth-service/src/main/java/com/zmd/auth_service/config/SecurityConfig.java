package com.zmd.auth_service.config;


import com.zmd.auth_service.api.error.ProblemUris;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

import static com.zmd.auth_service.api.error.ProblemFields.*;
import static com.zmd.auth_service.api.error.ProblemTypes.*;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint problemAuthEntryPoint,
            AccessDeniedHandler problemAccessDeniedHandler
    ) {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permit /error so validation and other MVC exceptions
                        // don't get intercepted by Spring Security and turned into 401.
                        // Spring forwards failed requests (e.g. 400) to /error internally.
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(problemAuthEntryPoint)
                        .accessDeniedHandler(problemAccessDeniedHandler)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint problemAuthEntryPoint(ObjectMapper om) {
        return (request, response, authException) ->
                writeProblem(response, om, request, HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        "Authentication is required to access this resource.",
                        ProblemUris.BASE + UNAUTHORIZED);
    }

    @Bean
    public AccessDeniedHandler problemAccessDeniedHandler(ObjectMapper om) {
        return (request, response, accessDeniedException) ->
                writeProblem(response, om, request, HttpStatus.FORBIDDEN,
                        "Access denied",
                        "You do not have permission to access this resource.",
                        ProblemUris.BASE + ACCESS_DENIED);
    }

    private static void writeProblem(
            HttpServletResponse response,
            ObjectMapper om,
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail,
            String type
    ) throws IOException {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(type));
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty(TIMESTAMP, OffsetDateTime.now());

        response.setStatus(status.value());
        response.setContentType("application/problem+json;charset=UTF-8");
        om.writeValue(response.getOutputStream(), pd);
    }

}
