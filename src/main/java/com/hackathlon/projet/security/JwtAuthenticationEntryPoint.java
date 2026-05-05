package com.hackathlon.projet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jwtError = (String) request.getAttribute("jwt_error");
        String message = jwtError != null
                ? jwtError
                : "Authentification requise. Connectez-vous via POST /api/auth/login";

        response.getWriter().write(buildJson(401, message));
    }

    private String buildJson(int status, String message) {
        return String.format(
                "{\"status\":%d,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status,
                message,
                LocalDateTime.now().format(FORMATTER));
    }
}
