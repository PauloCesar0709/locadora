package br.pucminas.aluguel.dto;

import java.util.List;

public record ErroResponse(String mensagem, List<String> detalhes) {
}
