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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Pedido de crédito feito pelo cliente a um banco para pagar a locação.
 * Ao aprovar, o banco define a taxa de juros diária (juros simples).
 */
@Entity
@Table(name = "contrato_credito")
public class ContratoCredito {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @Column(name = "valor_solicitado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "quantidade_dias", nullable = false)
    private Integer quantidadeDias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCredito status = StatusCredito.PENDENTE;

    /** Percentual ao dia (ex.: 0.5 = 0,5% ao dia). */
    @Column(name = "taxa_juros_diaria", precision = 7, scale = 4)
    private BigDecimal taxaJurosDiaria;

    @Column(name = "valor_total_com_juros", precision = 12, scale = 2)
    private BigDecimal valorTotalComJuros;

    @Column(name = "motivo_recusa", length = 500)
    private String motivoRecusa;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @PrePersist
    void aoCriar() {
        if (dataSolicitacao == null) {
            dataSolicitacao = LocalDateTime.now();
        }
    }

    /** Juros simples: valor x (1 + taxa/100 x dias). */
    public static BigDecimal calcularTotalComJuros(BigDecimal valor, BigDecimal taxaPercentualDia, int dias) {
        BigDecimal fator = BigDecimal.ONE.add(taxaPercentualDia.divide(CEM).multiply(BigDecimal.valueOf(dias)));
        return valor.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Banco getBanco() { return banco; }
    public void setBanco(Banco banco) { this.banco = banco; }

    public BigDecimal getValorSolicitado() { return valorSolicitado; }
    public void setValorSolicitado(BigDecimal valorSolicitado) { this.valorSolicitado = valorSolicitado; }

    public Integer getQuantidadeDias() { return quantidadeDias; }
    public void setQuantidadeDias(Integer quantidadeDias) { this.quantidadeDias = quantidadeDias; }

    public StatusCredito getStatus() { return status; }
    public void setStatus(StatusCredito status) { this.status = status; }

    public BigDecimal getTaxaJurosDiaria() { return taxaJurosDiaria; }
    public void setTaxaJurosDiaria(BigDecimal taxaJurosDiaria) { this.taxaJurosDiaria = taxaJurosDiaria; }

    public BigDecimal getValorTotalComJuros() { return valorTotalComJuros; }
    public void setValorTotalComJuros(BigDecimal valorTotalComJuros) { this.valorTotalComJuros = valorTotalComJuros; }

    public String getMotivoRecusa() { return motivoRecusa; }
    public void setMotivoRecusa(String motivoRecusa) { this.motivoRecusa = motivoRecusa; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }

    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }
}
