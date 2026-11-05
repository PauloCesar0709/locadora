package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RendimentoDto(
        @NotBlank(message = "Informe a entidade empregadora")
        @Size(max = 120, message = "Máximo de 120 caracteres")
        String entidadeEmpregadora,

        @NotNull(message = "Informe o rendimento")
        @DecimalMin(value = "0.00", message = "O rendimento não pode ser negativo")
        BigDecimal valor) {
}
