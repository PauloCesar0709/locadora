package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Altera o valor diário e/ou ativa/desativa a oferta. Pedidos já feitos mantêm o valor antigo. */
public record OfertaAtualizacaoRequest(
        @NotNull(message = "Informe o valor diário")
        @DecimalMin(value = "0.01", message = "O valor diário deve ser maior que zero")
        BigDecimal valorDiario,
        @NotNull(message = "Informe se a oferta está ativa") Boolean ativa) {
}
