package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.FormaPagamento;
import br.pucminas.aluguel.model.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        StatusPedido status,
        FormaPagamento formaPagamento,
        Long clienteId,
        String clienteNome,
        OfertaResponse oferta,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer quantidadeDias,
        BigDecimal valorDiario,
        BigDecimal valorTotal,
        LocalDateTime dataCriacao,
        LocalDateTime contratoExecutadoEm,
        List<CreditoResponse> creditos) {
}
