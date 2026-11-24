package br.pucminas.moeda.model;

import jakarta.persistence.*;

@Entity
public class EmpresaParceira extends Usuario {

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.EMPRESA; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
