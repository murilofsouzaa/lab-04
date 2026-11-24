package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.Vantagem;

public record VantagemResponse(Long id, String nome, String descricao, String foto, int custo,
                               Long empresaId, String empresa) {

    public static VantagemResponse de(Vantagem v) {
        return new VantagemResponse(v.getId(), v.getNome(), v.getDescricao(), v.getFoto(), v.getCusto(),
                v.getEmpresa().getId(), v.getEmpresa().getNome());
    }
}
