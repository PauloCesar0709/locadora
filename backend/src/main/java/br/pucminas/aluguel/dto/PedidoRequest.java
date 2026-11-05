package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Pedido de locação. bancoId é obrigatório quando formaPagamento = CREDITO. */
public record PedidoRequest(
        @NotNull(message = "Escolha uma oferta de locação") Long ofertaId,
        @NotNull(message = "Informe a data de início") LocalDate dataInicio,
        @NotNull(message = "Informe a data de fim") LocalDate dataFim,
        @NotNull(message = "Escolha a forma de pagamento") FormaPagamento formaPagamento,
        Long bancoId) {
}
