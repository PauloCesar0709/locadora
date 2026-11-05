package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "rendimento")
public class Rendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "entidade_empregadora", nullable = false, length = 120)
    private String entidadeEmpregadora;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    public Rendimento() {
    }

    public Rendimento(Cliente cliente, String entidadeEmpregadora, BigDecimal valor) {
        this.cliente = cliente;
        this.entidadeEmpregadora = entidadeEmpregadora;
        this.valor = valor;
    }

    public Long getId() { return id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getEntidadeEmpregadora() { return entidadeEmpregadora; }
    public void setEntidadeEmpregadora(String entidadeEmpregadora) { this.entidadeEmpregadora = entidadeEmpregadora; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
