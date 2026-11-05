package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Empresa cadastrada no sistema: Locadora ou Banco. */
@Entity
@Table(name = "agente")
public abstract class Agente extends Usuario {

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(length = 200)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
