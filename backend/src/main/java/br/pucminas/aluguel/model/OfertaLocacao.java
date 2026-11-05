package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Serviço de locação oferecido por uma locadora: um veículo com valor fixo diário. */
@Entity
@Table(name = "oferta_locacao")
public class OfertaLocacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locadora_id")
    private Locadora locadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @Column(name = "valor_diario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDiario;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    void aoCriar() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Locadora getLocadora() { return locadora; }
    public void setLocadora(Locadora locadora) { this.locadora = locadora; }

    public Automovel getAutomovel() { return automovel; }
    public void setAutomovel(Automovel automovel) { this.automovel = automovel; }

    public BigDecimal getValorDiario() { return valorDiario; }
    public void setValorDiario(BigDecimal valorDiario) { this.valorDiario = valorDiario; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
