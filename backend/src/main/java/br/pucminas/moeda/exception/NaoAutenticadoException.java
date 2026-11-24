package br.pucminas.moeda.exception;

public class NaoAutenticadoException extends RuntimeException {
    public NaoAutenticadoException(String mensagem) { super(mensagem); }
}
