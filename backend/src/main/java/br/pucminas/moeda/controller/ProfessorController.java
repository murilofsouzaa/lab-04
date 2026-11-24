package br.pucminas.moeda.controller;

import br.pucminas.moeda.config.AuthInterceptor;
import br.pucminas.moeda.dto.EnviarMoedasDTO;
import br.pucminas.moeda.dto.ExtratoResponse;
import br.pucminas.moeda.dto.TransacaoResponseDTO;
import br.pucminas.moeda.model.Professor;
import br.pucminas.moeda.model.Usuario;
import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.service.ExtratoService;
import br.pucminas.moeda.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;
    private final ExtratoService extratoService;
    private final AuthService authService;

    public ProfessorController(ProfessorService professorService, ExtratoService extratoService,
                               AuthService authService) {
        this.professorService = professorService;
        this.extratoService = extratoService;
        this.authService = authService;
    }

    @PostMapping("/{id}/enviar-moedas")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponseDTO enviarMoedas(@PathVariable Long id, @Valid @RequestBody EnviarMoedasDTO dto,
                                             @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Professor.class, id);
        return professorService.enviarMoedas(id, dto);
    }

    @GetMapping("/{id}/extrato")
    public ExtratoResponse extrato(@PathVariable Long id,
                                   @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Professor.class, id);
        return extratoService.doProfessor(id);
    }
}
