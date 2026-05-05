package com.hackathlon.projet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write(buildJson(403, "Accès refusé : vous n'avez pas les droits nécessaires"));
    }

    private String buildJson(int status, String message) {
        return String.format(
                "{\"status\":%d,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status,
                message,
                LocalDateTime.now().format(FORMATTER)
        );
    }
}
