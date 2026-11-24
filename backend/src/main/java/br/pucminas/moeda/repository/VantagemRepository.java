package br.pucminas.moeda.repository;

import br.pucminas.moeda.model.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    List<Vantagem> findByAtivaTrueOrderByNome();
    List<Vantagem> findByEmpresaIdAndAtivaTrueOrderByNome(Long empresaId);
}
