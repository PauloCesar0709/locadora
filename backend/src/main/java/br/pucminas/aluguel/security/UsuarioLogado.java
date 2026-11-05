package br.pucminas.aluguel.security;

import br.pucminas.aluguel.model.Perfil;
import org.springframework.security.oauth2.jwt.Jwt;

/** Dados do usuário autenticado, extraídos do token JWT. */
public record UsuarioLogado(Long id, Perfil perfil) {

    public static UsuarioLogado de(Jwt jwt) {
        return new UsuarioLogado(Long.valueOf(jwt.getSubject()), Perfil.valueOf(jwt.getClaimAsString("perfil")));
    }

    public boolean isCliente() {
        return perfil == Perfil.CLIENTE;
    }

    public boolean isLocadora() {
        return perfil == Perfil.LOCADORA;
    }

    public boolean isBanco() {
        return perfil == Perfil.BANCO;
    }
}
