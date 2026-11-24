package br.pucminas.moeda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// senha é obrigatória no cadastro e opcional na edição (vazia mantém a atual)
public record EmpresaRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String senha,
        @NotBlank String cnpj) {
}
