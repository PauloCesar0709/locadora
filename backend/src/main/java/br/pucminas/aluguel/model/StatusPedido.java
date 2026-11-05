package br.pucminas.aluguel.model;

/**
 * Fluxo do pedido de locação:
 * pagamento próprio -> CONCLUIDO;
 * crédito -> AGUARDANDO_CREDITO -> CONCLUIDO (banco aceita) | CREDITO_RECUSADO (banco recusa);
 * CREDITO_RECUSADO -> CONCLUIDO (cliente paga) | AGUARDANDO_CREDITO (outro banco);
 * qualquer status antes da data de início -> CANCELADO.
 */
public enum StatusPedido {
    AGUARDANDO_CREDITO,
    CREDITO_RECUSADO,
    CONCLUIDO,
    CANCELADO
}
