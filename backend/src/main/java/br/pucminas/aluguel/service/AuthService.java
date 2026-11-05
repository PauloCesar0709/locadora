package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.LoginRequest;
import br.pucminas.aluguel.dto.LoginResponse;
import br.pucminas.aluguel.exception.CredenciaisInvalidasException;
import br.pucminas.aluguel.model.Usuario;
import br.pucminas.aluguel.repository.UsuarioRepository;
import br.pucminas.aluguel.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login().trim())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }
        return new LoginResponse(jwtService.gerarToken(usuario), usuario.getId(), usuario.getNome(), usuario.getPerfil());
    }
}
