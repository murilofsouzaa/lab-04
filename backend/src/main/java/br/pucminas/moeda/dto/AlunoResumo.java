package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.Aluno;

public record AlunoResumo(Long id, String nome, String curso) {

    public static AlunoResumo de(Aluno a) {
        return new AlunoResumo(a.getId(), a.getNome(), a.getCurso());
    }
}
