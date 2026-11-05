package br.pucminas.aluguel.model;

/** Tipos de usuário. Cada perfil tem o seu próprio hub na aplicação. */
public enum Perfil {
    CLIENTE,
    LOCADORA,
    BANCO;

    public boolean isAgente() {
        return this != CLIENTE;
    }
}
