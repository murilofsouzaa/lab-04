package br.pucminas.moeda.model;

import br.pucminas.moeda.exception.RegraNegocioException;
import jakarta.persistence.*;

@Entity
public class Professor extends Usuario {

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String departamento;

    @ManyToOne(optional = false)
    private Instituicao instituicao;

    private int saldo;

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.PROFESSOR; }

    public void creditar(int valor) { saldo += valor; }

    public void debitar(int valor) {
        if (valor > saldo) throw new RegraNegocioException("Saldo insuficiente");
        saldo -= valor;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }
    public int getSaldo() { return saldo; }
}
