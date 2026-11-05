package br.pucminas.aluguel.dto;

/** Dados mínimos de um banco para o cliente escolher a quem pedir crédito. */
public record BancoResumo(Long id, String nome, String codigoBanco) {
}
