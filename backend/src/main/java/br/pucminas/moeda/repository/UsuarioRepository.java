package br.pucminas.moeda.repository;

import br.pucminas.moeda.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByToken(String token);
    boolean existsByEmail(String email);
}
