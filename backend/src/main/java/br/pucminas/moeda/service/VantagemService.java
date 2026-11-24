package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.VantagemRequest;
import br.pucminas.moeda.exception.AcessoNegadoException;
import br.pucminas.moeda.exception.NaoEncontradoException;
import br.pucminas.moeda.model.Vantagem;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VantagemService {

    private final VantagemRepository vantagemRepository;
    private final EmpresaService empresaService;

    public VantagemService(VantagemRepository vantagemRepository, EmpresaService empresaService) {
        this.vantagemRepository = vantagemRepository;
        this.empresaService = empresaService;
    }

    @Transactional(readOnly = true)
    public List<Vantagem> listarAtivas() {
        return vantagemRepository.findByAtivaTrueOrderByNome();
    }

    @Transactional(readOnly = true)
    public List<Vantagem> listarDaEmpresa(Long empresaId) {
        return vantagemRepository.findByEmpresaIdAndAtivaTrueOrderByNome(empresaId);
    }

    @Transactional
    public Vantagem cadastrar(Long empresaId, VantagemRequest req) {
        Vantagem vantagem = new Vantagem();
        vantagem.setEmpresa(empresaService.buscar(empresaId));
        vantagem.setNome(req.nome());
        vantagem.setDescricao(req.descricao());
        vantagem.setFoto(req.foto());
        vantagem.setCusto(req.custo());
        return vantagemRepository.save(vantagem);
    }

    @Transactional
    public void remover(Long empresaId, Long vantagemId) {
        Vantagem vantagem = vantagemRepository.findById(vantagemId)
                .orElseThrow(() -> new NaoEncontradoException("Vantagem não encontrada"));
        if (!vantagem.getEmpresa().getId().equals(empresaId)) throw new AcessoNegadoException("Acesso negado");
        vantagem.setAtiva(false);
        vantagemRepository.save(vantagem);
    }
}
