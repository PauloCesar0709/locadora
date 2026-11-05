package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Modificação das datas da locação (enquanto aguarda o banco ou após recusa). */
public record PedidoAtualizacaoRequest(
        @NotNull(message = "Informe a data de início") LocalDate dataInicio,
        @NotNull(message = "Informe a data de fim") LocalDate dataFim) {
}
