package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.ExtratoResponse;
import br.pucminas.moeda.dto.TransacaoResponseDTO;
import br.pucminas.moeda.model.Transacao;
import br.pucminas.moeda.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExtratoService {

    private final TransacaoRepository transacaoRepository;
    private final AlunoService alunoService;
    private final ProfessorService professorService;

    public ExtratoService(TransacaoRepository transacaoRepository, AlunoService alunoService,
                          ProfessorService professorService) {
        this.transacaoRepository = transacaoRepository;
        this.alunoService = alunoService;
        this.professorService = professorService;
    }

    /** Recebimentos e trocas do aluno. */
    @Transactional(readOnly = true)
    public ExtratoResponse doAluno(Long id) {
        int saldo = alunoService.buscar(id).getSaldo();
        return new ExtratoResponse(saldo, mapear(transacaoRepository.findByAlunoIdOrderByDataDesc(id)));
    }

    /** Envios e créditos semestrais do professor. */
    @Transactional(readOnly = true)
    public ExtratoResponse doProfessor(Long id) {
        int saldo = professorService.buscar(id).getSaldo();
        return new ExtratoResponse(saldo, mapear(transacaoRepository.findByProfessorIdOrderByDataDesc(id)));
    }

    /** Resgates das vantagens da empresa (para conferência dos cupons). */
    @Transactional(readOnly = true)
    public ExtratoResponse daEmpresa(Long id) {
        return new ExtratoResponse(null, mapear(transacaoRepository.findByVantagemEmpresaIdOrderByDataDesc(id)));
    }

    private List<TransacaoResponseDTO> mapear(List<Transacao> transacoes) {
        return transacoes.stream().map(TransacaoResponseDTO::de).toList();
    }
}
