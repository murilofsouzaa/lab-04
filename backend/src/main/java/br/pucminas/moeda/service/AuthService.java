package br.pucminas.moeda.service;

import br.pucminas.moeda.dto.LoginRequest;
import br.pucminas.moeda.dto.LoginResponse;
import br.pucminas.moeda.exception.AcessoNegadoException;
import br.pucminas.moeda.exception.NaoAutenticadoException;
import br.pucminas.moeda.exception.RegraNegocioException;
import br.pucminas.moeda.model.Usuario;
import br.pucminas.moeda.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.email())
                .filter(u -> encoder.matches(req.senha(), u.getSenha()))
                .orElseThrow(() -> new NaoAutenticadoException("E-mail ou senha inválidos"));
        usuario.setToken(UUID.randomUUID().toString());
        usuarioRepository.save(usuario);
        return new LoginResponse(usuario.getToken(), usuario.getTipo(), usuario.getId(), usuario.getNome());
    }

    @Transactional
    public void logout(Usuario usuario) {
        usuario.setToken(null);
        usuarioRepository.save(usuario);
    }

    public Usuario autenticar(String token) {
        if (token == null || token.isBlank()) throw new NaoAutenticadoException("Login necessário");
        return usuarioRepository.findByToken(token)
                .orElseThrow(() -> new NaoAutenticadoException("Sessão inválida ou expirada"));
    }

    /** Garante que o usuário logado é do tipo esperado e é o dono do recurso {id}. */
    public void exigirDono(Usuario usuario, Class<? extends Usuario> tipo, Long id) {
        if (!tipo.isInstance(usuario) || !usuario.getId().equals(id)) {
            throw new AcessoNegadoException("Acesso negado");
        }
    }

    public void validarEmailLivre(String email, Long idAtual) {
        usuarioRepository.findByEmail(email)
                .filter(u -> !u.getId().equals(idAtual))
                .ifPresent(u -> { throw new RegraNegocioException("E-mail já cadastrado"); });
    }

    public String hash(String senha) {
        return encoder.encode(senha);
    }

    /** Define a senha: obrigatória no cadastro; vazia na edição mantém a atual. */
    public void aplicarSenha(Usuario usuario, String senha) {
        if (senha != null && !senha.isBlank()) {
            if (senha.length() < 6) throw new RegraNegocioException("A senha deve ter pelo menos 6 caracteres");
            usuario.setSenha(hash(senha));
        } else if (usuario.getSenha() == null) {
            throw new RegraNegocioException("A senha é obrigatória");
        }
    }
}
