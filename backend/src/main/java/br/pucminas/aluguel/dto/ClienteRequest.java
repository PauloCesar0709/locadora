package br.pucminas.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Entrada de cadastro e atualização de cliente.
 * "login" e "senha" são obrigatórios no cadastro e opcionais na atualização
 * (senha em branco mantém a senha atual; o login não pode ser alterado).
 */
public record ClienteRequest(
        @Size(max = 60, message = "Máximo de 60 caracteres")
        String login,

        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha,

        @NotBlank(message = "Informe o nome")
        @Size(max = 120, message = "Máximo de 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o CPF")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos")
        String cpf,

        @NotBlank(message = "Informe o RG")
        @Size(max = 20, message = "Máximo de 20 caracteres")
        String rg,

        @NotBlank(message = "Informe o endereço")
        @Size(max = 200, message = "Máximo de 200 caracteres")
        String endereco,

        @Size(max = 80, message = "Máximo de 80 caracteres")
        String profissao,

        @Valid
        @Size(max = 3, message = "Informe no máximo 3 rendimentos")
        List<RendimentoDto> rendimentos) {
}
