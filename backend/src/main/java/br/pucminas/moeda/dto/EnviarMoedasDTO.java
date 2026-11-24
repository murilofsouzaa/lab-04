package br.pucminas.moeda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnviarMoedasDTO(
        @NotNull Long idAluno,
        @Positive int valor,
        @NotBlank(message = "o motivo é obrigatório") String motivo) {
}
