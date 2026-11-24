package br.pucminas.moeda.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    private int valor;

    @Column(length = 1000)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @ManyToOne
    private Professor professor;

    @ManyToOne
    private Aluno aluno;

    @ManyToOne
    private Vantagem vantagem;

    private String codigoCupom;

    public static Transacao envio(Professor professor, Aluno aluno, int valor, String motivo) {
        Transacao t = new Transacao();
        t.tipo = TipoTransacao.ENVIO;
        t.professor = professor;
        t.aluno = aluno;
        t.valor = valor;
        t.motivo = motivo;
        return t;
    }

    public static Transacao resgate(Aluno aluno, Vantagem vantagem, String codigoCupom) {
        Transacao t = new Transacao();
        t.tipo = TipoTransacao.RESGATE;
        t.aluno = aluno;
        t.vantagem = vantagem;
        t.valor = vantagem.getCusto();
        t.codigoCupom = codigoCupom;
        return t;
    }

    public static Transacao creditoSemestral(Professor professor, int valor) {
        Transacao t = new Transacao();
        t.tipo = TipoTransacao.CREDITO_SEMESTRAL;
        t.professor = professor;
        t.valor = valor;
        t.motivo = "Crédito semestral";
        return t;
    }

    public Long getId() { return id; }
    public TipoTransacao getTipo() { return tipo; }
    public int getValor() { return valor; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getData() { return data; }
    public Professor getProfessor() { return professor; }
    public Aluno getAluno() { return aluno; }
    public Vantagem getVantagem() { return vantagem; }
    public String getCodigoCupom() { return codigoCupom; }
}
