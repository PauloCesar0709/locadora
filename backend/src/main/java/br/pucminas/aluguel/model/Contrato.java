package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Contrato de locação, gerado quando a locação é concluída. */
@Entity
@Table(name = "contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    /** Contrato de crédito aprovado (nulo quando o cliente paga por conta própria). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 20)
    private FormaPagamento formaPagamento;

    @Column(name = "data_execucao", nullable = false)
    private LocalDateTime dataExecucao;

    @PrePersist
    void aoCriar() {
        if (dataExecucao == null) {
            dataExecucao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public ContratoCredito getContratoCredito() { return contratoCredito; }
    public void setContratoCredito(ContratoCredito contratoCredito) { this.contratoCredito = contratoCredito; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public LocalDateTime getDataExecucao() { return dataExecucao; }
}
