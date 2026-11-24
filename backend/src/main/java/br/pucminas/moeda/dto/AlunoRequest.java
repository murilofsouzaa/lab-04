package br.pucminas.moeda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// senha é obrigatória no cadastro e opcional na edição (vazia mantém a atual)
public record AlunoRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String senha,
        @NotBlank String cpf,
        @NotBlank String rg,
        @NotBlank String endereco,
        @NotBlank String curso,
        @NotNull Long instituicaoId) {
}
