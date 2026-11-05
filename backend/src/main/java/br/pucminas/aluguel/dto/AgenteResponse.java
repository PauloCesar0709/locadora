package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Perfil;

public record AgenteResponse(
        Long id,
        String login,
        String nome,
        Perfil perfil,
        String cnpj,
        String endereco,
        String telefone,
        String codigoBanco) {
}
