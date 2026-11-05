package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Cadastro público de empresa: o tipo escolhido é LOCADORA ou BANCO. */
public record AgenteCadastroRequest(
        @NotNull(message = "Escolha o tipo de empresa")
        Perfil tipo,

        @NotBlank(message = "Informe o usuário")
        @Size(max = 60, message = "Máximo de 60 caracteres")
        String login,

        @NotBlank(message = "Informe a senha")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha,

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

        /** Obrigatório quando o tipo é BANCO. */
        @Size(max = 10, message = "Máximo de 10 caracteres")
        String codigoBanco) {
}
