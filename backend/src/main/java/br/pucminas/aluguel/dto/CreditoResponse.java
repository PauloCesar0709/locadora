package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.StatusCredito;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Pedido de crédito, com os dados do cliente e da locação para a análise do banco. */
public record CreditoResponse(
        Long id,
        StatusCredito status,
        Long pedidoId,
        Long bancoId,
        String bancoNome,
        BigDecimal valorSolicitado,
        Integer quantidadeDias,
        BigDecimal taxaJurosDiaria,
        BigDecimal valorTotalComJuros,
        String motivoRecusa,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataResposta,
        String veiculo,
        String locadoraNome,
        LocalDate dataInicio,
        LocalDate dataFim,
        ClienteResponse cliente) {
}
