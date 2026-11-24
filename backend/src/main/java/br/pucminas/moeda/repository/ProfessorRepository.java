package br.pucminas.moeda.repository;

import br.pucminas.moeda.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
