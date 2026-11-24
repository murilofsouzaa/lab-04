package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.EmpresaParceira;

public record EmpresaResponse(Long id, String nome, String email, String cnpj) {

    public static EmpresaResponse de(EmpresaParceira e) {
        return new EmpresaResponse(e.getId(), e.getNome(), e.getEmail(), e.getCnpj());
    }
}
