package br.pucminas.aluguel.dto;

import java.util.List;

public record ClienteResponse(
        Long id,
        String login,
        String nome,
        String cpf,
        String rg,
        String endereco,
        String profissao,
        List<RendimentoDto> rendimentos) {
}
