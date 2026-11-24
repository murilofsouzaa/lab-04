package br.pucminas.moeda.controller;

import br.pucminas.moeda.config.AuthInterceptor;
import br.pucminas.moeda.dto.*;
import br.pucminas.moeda.model.EmpresaParceira;
import br.pucminas.moeda.model.Usuario;
import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.service.EmpresaService;
import br.pucminas.moeda.service.ExtratoService;
import br.pucminas.moeda.service.VantagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final VantagemService vantagemService;
    private final ExtratoService extratoService;
    private final AuthService authService;

    public EmpresaController(EmpresaService empresaService, VantagemService vantagemService,
                             ExtratoService extratoService, AuthService authService) {
        this.empresaService = empresaService;
        this.vantagemService = vantagemService;
        this.extratoService = extratoService;
        this.authService = authService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse cadastrar(@Valid @RequestBody EmpresaRequest req) {
        return EmpresaResponse.de(empresaService.cadastrar(req));
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscar(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        return EmpresaResponse.de(empresaService.buscar(id));
    }

    @PutMapping("/{id}")
    public EmpresaResponse atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest req,
                                     @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        return EmpresaResponse.de(empresaService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        empresaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/vantagens")
    public List<VantagemResponse> vantagens(@PathVariable Long id,
                                            @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        return vantagemService.listarDaEmpresa(id).stream().map(VantagemResponse::de).toList();
    }

    @PostMapping("/{id}/vantagens")
    @ResponseStatus(HttpStatus.CREATED)
    public VantagemResponse cadastrarVantagem(@PathVariable Long id, @Valid @RequestBody VantagemRequest req,
                                              @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        return VantagemResponse.de(vantagemService.cadastrar(id, req));
    }

    @DeleteMapping("/{id}/vantagens/{idVantagem}")
    public ResponseEntity<Void> removerVantagem(@PathVariable Long id, @PathVariable Long idVantagem,
                                                @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        vantagemService.remover(id, idVantagem);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/extrato")
    public ExtratoResponse extrato(@PathVariable Long id,
                                   @RequestAttribute(AuthInterceptor.USUARIO) Usuario usuario) {
        authService.exigirDono(usuario, EmpresaParceira.class, id);
        return extratoService.daEmpresa(id);
    }
}
