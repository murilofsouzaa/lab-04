package br.pucminas.moeda.dto;

import br.pucminas.moeda.model.TipoTransacao;
import br.pucminas.moeda.model.Transacao;

import java.time.LocalDateTime;

public record TransacaoResponseDTO(Long id, TipoTransacao tipo, int valor, String motivo, LocalDateTime data,
                                   String professor, String aluno, String vantagem, String codigoCupom) {

    public static TransacaoResponseDTO de(Transacao t) {
        return new TransacaoResponseDTO(t.getId(), t.getTipo(), t.getValor(), t.getMotivo(), t.getData(),
                t.getProfessor() != null ? t.getProfessor().getNome() : null,
                t.getAluno() != null ? t.getAluno().getNome() : null,
                t.getVantagem() != null ? t.getVantagem().getNome() : null,
                t.getCodigoCupom());
    }
}
