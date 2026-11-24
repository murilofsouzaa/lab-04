package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.EmpresaRequest;
import br.pucminas.moeda.exception.NaoEncontradoException;
import br.pucminas.moeda.exception.RegraNegocioException;
import br.pucminas.moeda.model.EmpresaParceira;
import br.pucminas.moeda.repository.EmpresaParceiraRepository;
import br.pucminas.moeda.repository.TransacaoRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaParceiraRepository empresaRepository;
    private final VantagemRepository vantagemRepository;
    private final TransacaoRepository transacaoRepository;
    private final AuthService authService;

    public EmpresaService(EmpresaParceiraRepository empresaRepository, VantagemRepository vantagemRepository,
                          TransacaoRepository transacaoRepository, AuthService authService) {
        this.empresaRepository = empresaRepository;
        this.vantagemRepository = vantagemRepository;
        this.transacaoRepository = transacaoRepository;
        this.authService = authService;
    }

    @Transactional
    public EmpresaParceira cadastrar(EmpresaRequest req) {
        return salvar(new EmpresaParceira(), req);
    }

    @Transactional
    public EmpresaParceira atualizar(Long id, EmpresaRequest req) {
        return salvar(buscar(id), req);
    }

    @Transactional(readOnly = true)
    public EmpresaParceira buscar(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Empresa não encontrada"));
    }

    @Transactional
    public void excluir(Long id) {
        EmpresaParceira empresa = buscar(id);
        if (transacaoRepository.existsByVantagemEmpresaId(id)) {
            throw new RegraNegocioException("Empresa possui vantagens resgatadas e não pode ser excluída");
        }
        vantagemRepository.deleteAll(vantagemRepository.findByEmpresaIdAndAtivaTrueOrderByNome(id));
        empresaRepository.delete(empresa);
    }

    private EmpresaParceira salvar(EmpresaParceira empresa, EmpresaRequest req) {
        authService.validarEmailLivre(req.email(), empresa.getId());
        empresa.setNome(req.nome());
        empresa.setEmail(req.email());
        empresa.setCnpj(req.cnpj());
        authService.aplicarSenha(empresa, req.senha());
        return empresaRepository.save(empresa);
    }
}
