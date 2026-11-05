package br.pucminas.aluguel.dto;

import java.math.BigDecimal;

public record OfertaResponse(
        Long id,
        Long locadoraId,
        String locadoraNome,
        AutomovelResponse automovel,
        BigDecimal valorDiario,
        boolean ativa) {
}
