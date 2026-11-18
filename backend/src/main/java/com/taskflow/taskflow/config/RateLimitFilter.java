package com.taskflow.taskflow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita tentativas em /api/auth/** por IP (janela fixa de 1 minuto).
 * Versão simples, em memória: serve para uma instância só.
 * Com várias instâncias, o ideal é Redis ou um API Gateway.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private record Counter(Instant windowStart, int count) {}

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Instant now = Instant.now();

        Counter updated = counters.merge(ip, new Counter(now, 1), (old, ignored) ->
                Duration.between(old.windowStart(), now).compareTo(WINDOW) > 0
                        ? new Counter(now, 1)
                        : new Counter(old.windowStart(), old.count() + 1));

        if (updated.count() > MAX_REQUESTS) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"status\":429,\"message\":\"Muitas tentativas. Aguarde um minuto.\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
