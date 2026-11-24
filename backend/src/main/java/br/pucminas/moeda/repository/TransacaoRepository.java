package br.pucminas.moeda.repository;

import br.pucminas.moeda.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByProfessorIdOrderByDataDesc(Long professorId);
    List<Transacao> findByAlunoIdOrderByDataDesc(Long alunoId);
    List<Transacao> findByVantagemEmpresaIdOrderByDataDesc(Long empresaId);
    boolean existsByAlunoId(Long alunoId);
    boolean existsByVantagemEmpresaId(Long empresaId);
}
