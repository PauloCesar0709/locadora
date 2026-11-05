package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Banco agente: analisa pedidos de crédito e define a taxa de juros diária. */
@Entity
@Table(name = "banco")
public class Banco extends Agente {

    @Column(name = "codigo_banco", nullable = false, length = 10)
    private String codigoBanco;

    public Banco() {
        setPerfil(Perfil.BANCO);
    }

    public String getCodigoBanco() { return codigoBanco; }
    public void setCodigoBanco(String codigoBanco) { this.codigoBanco = codigoBanco; }
}
