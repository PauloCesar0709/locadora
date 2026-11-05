package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.Size;

public record RecusaCreditoRequest(@Size(max = 500, message = "Máximo de 500 caracteres") String motivo) {
}
