package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoCreditoRequest(@NotNull(message = "Escolha um banco") Long bancoId) {
}
