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

@Entity
@Table(name = "automovel")
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Locadora que cadastrou e gerencia o veículo. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locadora_id")
    private Locadora locadora;

    /** Proprietário: Cliente, Locadora ou Banco. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    public Long getId() { return id; }

    public Locadora getLocadora() { return locadora; }
    public void setLocadora(Locadora locadora) { this.locadora = locadora; }

    public Usuario getProprietario() { return proprietario; }
    public void setProprietario(Usuario proprietario) { this.proprietario = proprietario; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}
