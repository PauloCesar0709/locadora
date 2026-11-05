package br.pucminas.aluguel.exception;

/** Violação de regra de negócio (HTTP 400). */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
