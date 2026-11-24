package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.TipoUsuario;

public record LoginResponse(String token, TipoUsuario tipo, Long id, String nome) {
}
