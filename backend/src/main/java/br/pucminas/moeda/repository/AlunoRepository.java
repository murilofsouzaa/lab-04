package br.pucminas.moeda.repository;

import br.pucminas.moeda.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByInstituicaoIdOrderByNome(Long instituicaoId);
}
