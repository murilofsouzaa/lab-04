package br.pucminas.moeda.dto;

import java.util.List;

// saldo é nulo para empresas parceiras (o extrato delas lista os resgates das suas vantagens)
public record ExtratoResponse(Integer saldo, List<TransacaoResponseDTO> transacoes) {
}
