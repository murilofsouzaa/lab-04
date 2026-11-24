package br.pucminas.moeda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record VantagemRequest(
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotBlank(message = "a foto é obrigatória") String foto,
        @Positive int custo) {
}
