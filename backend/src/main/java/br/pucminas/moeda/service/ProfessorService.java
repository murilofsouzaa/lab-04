package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.EnviarMoedasDTO;
import br.pucminas.moeda.dto.TransacaoResponseDTO;
import br.pucminas.moeda.exception.NaoEncontradoException;
import br.pucminas.moeda.exception.RegraNegocioException;
import br.pucminas.moeda.model.Aluno;
import br.pucminas.moeda.model.Professor;
import br.pucminas.moeda.model.Transacao;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.ProfessorRepository;
import br.pucminas.moeda.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorService {

    public static final int MOEDAS_POR_SEMESTRE = 1000;

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final EmailService emailService;

    public ProfessorService(ProfessorRepository professorRepository, AlunoRepository alunoRepository,
                            TransacaoRepository transacaoRepository, EmailService emailService) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public Professor buscar(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Professor não encontrado"));
    }

    /** Diagrama de Sequência A: Enviar Moedas. */
    @Transactional
    public TransacaoResponseDTO enviarMoedas(Long idProfessor, EnviarMoedasDTO dto) {
        Professor professor = buscar(idProfessor);
        Aluno aluno = alunoRepository.findById(dto.idAluno())
                .orElseThrow(() -> new NaoEncontradoException("Aluno não encontrado"));
        if (!aluno.getInstituicao().getId().equals(professor.getInstituicao().getId())) {
            throw new RegraNegocioException("O aluno não pertence à instituição do professor");
        }

        validaSaldo(professor, dto.valor());
        atualizaSaldos(professor, aluno, dto.valor());

        Transacao transacao = transacaoRepository.save(
                Transacao.envio(professor, aluno, dto.valor(), dto.motivo().trim()));
        professorRepository.save(professor);
        alunoRepository.save(aluno);

        emailService.enviarEmailRecebimentoMoedas(aluno, professor, dto.valor(), dto.motivo().trim());
        return TransacaoResponseDTO.de(transacao);
    }

    /** Adiciona 1.000 moedas ao saldo corrente de cada professor (acumulável). */
    @Transactional
    public void creditarSemestre() {
        for (Professor professor : professorRepository.findAll()) {
            professor.creditar(MOEDAS_POR_SEMESTRE);
            transacaoRepository.save(Transacao.creditoSemestral(professor, MOEDAS_POR_SEMESTRE));
        }
    }

    private void validaSaldo(Professor professor, int valor) {
        if (professor.getSaldo() < valor) throw new RegraNegocioException("Saldo insuficiente");
    }

    private void atualizaSaldos(Professor professor, Aluno aluno, int valor) {
        professor.debitar(valor);
        aluno.creditar(valor);
    }
}
