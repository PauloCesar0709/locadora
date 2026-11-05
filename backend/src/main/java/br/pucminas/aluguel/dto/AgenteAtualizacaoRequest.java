package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Atualização dos dados da própria empresa. Senha em branco mantém a atual. */
public record AgenteAtualizacaoRequest(
        @NotBlank(message = "Informe a razão social")
        @Size(max = 120, message = "Máximo de 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o CNPJ")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 dígitos")
        String cnpj,

        @Size(max = 200, message = "Máximo de 200 caracteres")
        String endereco,

        @Size(max = 20, message = "Máximo de 20 caracteres")
        String telefone,

        @Size(max = 10, message = "Máximo de 10 caracteres")
        String codigoBanco,

        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha) {
}
