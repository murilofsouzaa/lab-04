package br.pucminas.moeda.model;

import jakarta.persistence.*;

@Entity
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 1000)
    private String descricao;

    // URL da imagem ou data URL (base64) enviada pelo frontend
    @Column(nullable = false, length = 1_000_000)
    private String foto;

    private int custo;

    // Remoção lógica: vantagens já resgatadas continuam referenciadas no extrato
    private boolean ativa = true;

    @ManyToOne(optional = false)
    private EmpresaParceira empresa;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public int getCusto() { return custo; }
    public void setCusto(int custo) { this.custo = custo; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public EmpresaParceira getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaParceira empresa) { this.empresa = empresa; }
}
