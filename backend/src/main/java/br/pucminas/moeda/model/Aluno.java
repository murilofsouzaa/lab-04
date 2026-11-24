package br.pucminas.moeda.model;

import br.pucminas.moeda.exception.RegraNegocioException;
import jakarta.persistence.*;

@Entity
public class Aluno extends Usuario {

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String curso;

    @ManyToOne(optional = false)
    private Instituicao instituicao;

    private int saldo;

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.ALUNO; }

    public void creditar(int valor) { saldo += valor; }

    public void debitar(int valor) {
        if (valor > saldo) throw new RegraNegocioException("Saldo insuficiente");
        saldo -= valor;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }
    public int getSaldo() { return saldo; }
}
