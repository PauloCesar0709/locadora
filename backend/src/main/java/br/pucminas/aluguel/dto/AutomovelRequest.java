package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Perfil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cadastro de veículo pela locadora.
 * tipoProprietario: LOCADORA (a própria locadora), BANCO (informe bancoId) ou CLIENTE (informe clienteCpf).
 */
public record AutomovelRequest(
        @NotBlank(message = "Informe a matrícula")
        @Size(max = 30, message = "Máximo de 30 caracteres")
        String matricula,

        @NotNull(message = "Informe o ano")
        @Min(value = 1900, message = "Ano inválido")
        @Max(value = 2100, message = "Ano inválido")
        Integer ano,

        @NotBlank(message = "Informe a marca")
        @Size(max = 60, message = "Máximo de 60 caracteres")
        String marca,

        @NotBlank(message = "Informe o modelo")
        @Size(max = 60, message = "Máximo de 60 caracteres")
        String modelo,

        @NotBlank(message = "Informe a placa")
        @Pattern(regexp = "[A-Za-z0-9]{7}", message = "A placa deve ter 7 caracteres (letras e números)")
        String placa,

        @NotNull(message = "Informe o tipo de proprietário")
        Perfil tipoProprietario,

        Long bancoId,

        @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos")
        String clienteCpf) {
}
