package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.Aluno;

public record AlunoResponse(Long id, String nome, String email, String cpf, String rg, String endereco,
                            String curso, Long instituicaoId, String instituicao, int saldo) {

    public static AlunoResponse de(Aluno a) {
        return new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getCpf(), a.getRg(), a.getEndereco(),
                a.getCurso(), a.getInstituicao().getId(), a.getInstituicao().getNome(), a.getSaldo());
    }
}
