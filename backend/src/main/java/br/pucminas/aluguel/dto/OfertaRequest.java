package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OfertaRequest(
        @NotNull(message = "Escolha o veículo") Long automovelId,
        @NotNull(message = "Informe o valor diário")
        @DecimalMin(value = "0.01", message = "O valor diário deve ser maior que zero")
        BigDecimal valorDiario) {
}
