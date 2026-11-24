package br.pucminas.moeda.config;

import br.pucminas.moeda.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Exige "Authorization: Bearer <token>" e disponibiliza o usuário logado como atributo "usuario". */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USUARIO = "usuario";

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod()) || rotaPublica(request)) return true;
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
        request.setAttribute(USUARIO, authService.autenticar(token));
        return true;
    }

    private boolean rotaPublica(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String metodo = request.getMethod();
        return uri.equals("/api/auth/login")
                || (metodo.equals("GET") && uri.equals("/api/instituicoes"))
                || (metodo.equals("POST") && (uri.equals("/api/alunos") || uri.equals("/api/empresas")));
    }
}
