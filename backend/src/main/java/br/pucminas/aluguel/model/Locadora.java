package br.pucminas.aluguel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Empresa que cadastra veículos e oferece o serviço de locação. */
@Entity
@Table(name = "locadora")
public class Locadora extends Agente {

    public Locadora() {
        setPerfil(Perfil.LOCADORA);
    }
}
