package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.AlunoRequest;
import br.pucminas.moeda.dto.TransacaoResponseDTO;
import br.pucminas.moeda.exception.NaoEncontradoException;
import br.pucminas.moeda.exception.RegraNegocioException;
import br.pucminas.moeda.model.Aluno;
import br.pucminas.moeda.model.Instituicao;
import br.pucminas.moeda.model.Transacao;
import br.pucminas.moeda.model.Vantagem;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.InstituicaoRepository;
import br.pucminas.moeda.repository.TransacaoRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final VantagemRepository vantagemRepository;
    private final TransacaoRepository transacaoRepository;
    private final AuthService authService;
    private final EmailService emailService;

    public AlunoService(AlunoRepository alunoRepository, InstituicaoRepository instituicaoRepository,
                        VantagemRepository vantagemRepository, TransacaoRepository transacaoRepository,
                        AuthService authService, EmailService emailService) {
        this.alunoRepository = alunoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.vantagemRepository = vantagemRepository;
        this.transacaoRepository = transacaoRepository;
        this.authService = authService;
        this.emailService = emailService;
    }

    @Transactional
    public Aluno cadastrar(AlunoRequest req) {
        return salvar(new Aluno(), req);
    }

    @Transactional
    public Aluno atualizar(Long id, AlunoRequest req) {
        return salvar(buscar(id), req);
    }

    @Transactional(readOnly = true)
    public Aluno buscar(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Aluno não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarPorInstituicao(Long instituicaoId) {
        return alunoRepository.findByInstituicaoIdOrderByNome(instituicaoId);
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = buscar(id);
        if (transacaoRepository.existsByAlunoId(id)) {
            throw new RegraNegocioException("Aluno possui transações e não pode ser excluído");
        }
        alunoRepository.delete(aluno);
    }

    /** Diagrama de Sequência B: Resgatar Vantagem. */
    @Transactional
    public TransacaoResponseDTO resgatarVantagem(Long idAluno, Long idVantagem) {
        Aluno aluno = buscar(idAluno);
        Vantagem vantagem = vantagemRepository.findById(idVantagem)
                .filter(Vantagem::isAtiva)
                .orElseThrow(() -> new NaoEncontradoException("Vantagem não encontrada"));

        validaSaldo(aluno, vantagem.getCusto());
        descontarSaldo(aluno, vantagem.getCusto());
        String codigo = geraCodigoCupom();

        Transacao transacao = transacaoRepository.save(Transacao.resgate(aluno, vantagem, codigo));
        alunoRepository.save(aluno);

        emailService.enviarEmailCupomAluno(aluno, vantagem, codigo);
        emailService.enviarEmailNotificacaoEmpresa(vantagem.getEmpresa(), aluno, vantagem, codigo);
        return TransacaoResponseDTO.de(transacao);
    }

    private void validaSaldo(Aluno aluno, int custo) {
        if (aluno.getSaldo() < custo) throw new RegraNegocioException("Saldo insuficiente para esta vantagem");
    }

    private void descontarSaldo(Aluno aluno, int custo) {
        aluno.debitar(custo);
    }

    private String geraCodigoCupom() {
        return "MOE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private Aluno salvar(Aluno aluno, AlunoRequest req) {
        authService.validarEmailLivre(req.email(), aluno.getId());
        Instituicao instituicao = instituicaoRepository.findById(req.instituicaoId())
                .orElseThrow(() -> new NaoEncontradoException("Instituição não encontrada"));
        aluno.setNome(req.nome());
        aluno.setEmail(req.email());
        aluno.setCpf(req.cpf());
        aluno.setRg(req.rg());
        aluno.setEndereco(req.endereco());
        aluno.setCurso(req.curso());
        aluno.setInstituicao(instituicao);
        authService.aplicarSenha(aluno, req.senha());
        return alunoRepository.save(aluno);
    }
}
