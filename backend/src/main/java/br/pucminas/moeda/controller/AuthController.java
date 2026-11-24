package br.pucminas.moeda.controller;

import br.pucminas.moeda.config.AuthInterceptor;
import br.pucminas.moeda.dto.LoginRequest;
import br.pucminas.moeda.dto.LoginResponse;
import br.pucminas.moeda.model.Usuario;
import br.pucminas.moeda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.logout(usuario);
        return ResponseEntity.noContent().build();
    }
}
