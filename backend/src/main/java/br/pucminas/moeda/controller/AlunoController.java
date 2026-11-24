package br.pucminas.moeda.controller;

import br.pucminas.moeda.config.AuthInterceptor;
import br.pucminas.moeda.dto.*;
import br.pucminas.moeda.exception.AcessoNegadoException;
import br.pucminas.moeda.model.Aluno;
import br.pucminas.moeda.model.Professor;
import br.pucminas.moeda.model.Usuario;
import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.service.ExtratoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final ExtratoService extratoService;
    private final AuthService authService;

    public AlunoController(AlunoService alunoService, ExtratoService extratoService, AuthService authService) {
        this.alunoService = alunoService;
        this.extratoService = extratoService;
        this.authService = authService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponse cadastrar(@Valid @RequestBody AlunoRequest req) {
        return AlunoResponse.de(alunoService.cadastrar(req));
    }

    /** Usado pelo professor para escolher o aluno: lista apenas alunos da sua instituição. */
    @GetMapping
    public List<AlunoResumo> listar(@RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        if (!(usuario instanceof Professor professor)) throw new AcessoNegadoException("Acesso negado");
        return alunoService.listarPorInstituicao(professor.getInstituicao().getId())
                .stream().map(AlunoResumo::de).toList();
    }

    @GetMapping("/{id}")
    public AlunoResponse buscar(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Aluno.class, id);
        return AlunoResponse.de(alunoService.buscar(id));
    }

    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequest req,
                                   @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Aluno.class, id);
        return AlunoResponse.de(alunoService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Aluno.class, id);
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/extrato")
    public ExtratoResponse extrato(@PathVariable Long id,
                                   @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Aluno.class, id);
        return extratoService.doAluno(id);
    }

    @PostMapping("/{id}/resgatar-vantagem/{idVantagem}")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponseDTO resgatarVantagem(@PathVariable Long id, @PathVariable Long idVantagem,
                                                 @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, Aluno.class, id);
        return alunoService.resgatarVantagem(id, idVantagem);
    }
}
