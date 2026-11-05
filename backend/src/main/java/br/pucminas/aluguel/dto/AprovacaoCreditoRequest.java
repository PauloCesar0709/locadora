package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Taxa de juros diária em percentual (ex.: 0.5 = 0,5% ao dia). */
public record AprovacaoCreditoRequest(
        @NotNull(message = "Informe a taxa de juros diária")
        @DecimalMin(value = "0.0", message = "A taxa não pode ser negativa")
        @DecimalMax(value = "100.0", message = "A taxa deve ser no máximo 100% ao dia")
        BigDecimal taxaJurosDiaria) {
}
