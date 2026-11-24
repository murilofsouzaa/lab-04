package br.pucminas.moeda.controller;

import br.pucminas.moeda.model.Instituicao;
import br.pucminas.moeda.repository.InstituicaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
public class InstituicaoController {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoController(InstituicaoRepository instituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
    }

    @GetMapping
    public List<Instituicao> listar() {
        return instituicaoRepository.findAll(Sort.by("nome"));
    }
}
