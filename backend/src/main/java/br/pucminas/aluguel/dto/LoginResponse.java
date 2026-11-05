package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Perfil;

public record LoginResponse(String token, Long id, String nome, Perfil perfil) {
}
