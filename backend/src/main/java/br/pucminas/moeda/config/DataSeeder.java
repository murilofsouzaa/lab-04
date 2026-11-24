package br.pucminas.moeda.config;

import br.pucminas.moeda.dto.AlunoRequest;
import br.pucminas.moeda.dto.EmpresaRequest;
import br.pucminas.moeda.dto.VantagemRequest;
import br.pucminas.moeda.model.EmpresaParceira;
import br.pucminas.moeda.model.Instituicao;
import br.pucminas.moeda.model.Professor;
import br.pucminas.moeda.repository.InstituicaoRepository;
import br.pucminas.moeda.repository.ProfessorRepository;
import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.service.EmpresaService;
import br.pucminas.moeda.service.ProfessorService;
import br.pucminas.moeda.service.VantagemService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Pré-cadastro exigido pelo enunciado (instituições e professores) e dados de demonstração.
 * Executa apenas com o banco vazio. Senha de todos os usuários: 123456.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    public static final String SENHA_PADRAO = "123456";

    private final InstituicaoRepository instituicaoRepository;
    private final ProfessorRepository professorRepository;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final EmpresaService empresaService;
    private final VantagemService vantagemService;
    private final AuthService authService;

    public DataSeeder(InstituicaoRepository instituicaoRepository, ProfessorRepository professorRepository,
                      ProfessorService professorService, AlunoService alunoService, EmpresaService empresaService,
                      VantagemService vantagemService, AuthService authService) {
        this.instituicaoRepository = instituicaoRepository;
        this.professorRepository = professorRepository;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.empresaService = empresaService;
        this.vantagemService = vantagemService;
        this.authService = authService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (instituicaoRepository.count() > 0) return;

        Instituicao puc = instituicaoRepository.save(new Instituicao("PUC Minas"));
        Instituicao ufmg = instituicaoRepository.save(new Instituicao("UFMG"));

        professor("Ana Souza", "ana.souza@pucminas.br", "111.111.111-11", "Engenharia de Software", puc);
        professor("Carlos Lima", "carlos.lima@pucminas.br", "222.222.222-22", "Ciência da Computação", puc);
        professor("Beatriz Rocha", "beatriz.rocha@ufmg.br", "333.333.333-33", "Sistemas de Informação", ufmg);
        professorService.creditarSemestre();

        alunoService.cadastrar(new AlunoRequest("João Silva", "joao@aluno.com", SENHA_PADRAO,
                "444.444.444-44", "MG-12.345.678", "Rua A, 100 - Belo Horizonte", "Engenharia de Software", puc.getId()));
        alunoService.cadastrar(new AlunoRequest("Maria Oliveira", "maria@aluno.com", SENHA_PADRAO,
                "555.555.555-55", "MG-98.765.432", "Rua B, 200 - Belo Horizonte", "Ciência da Computação", puc.getId()));

        EmpresaParceira cantina = empresaService.cadastrar(
                new EmpresaRequest("Cantina Universitária", "cantina@empresa.com", SENHA_PADRAO, "12.345.678/0001-90"));
        EmpresaParceira livraria = empresaService.cadastrar(
                new EmpresaRequest("Livraria Saber", "livraria@empresa.com", SENHA_PADRAO, "98.765.432/0001-10"));

        vantagemService.cadastrar(cantina.getId(), new VantagemRequest("Almoço com 20% de desconto",
                "Desconto de 20% em qualquer prato do restaurante universitário.",
                foto("🍽️", "#f4a261"), 50));
        vantagemService.cadastrar(cantina.getId(), new VantagemRequest("Café grátis",
                "Um café expresso grátis na cantina.",
                foto("☕", "#8d6e63"), 15));
        vantagemService.cadastrar(livraria.getId(), new VantagemRequest("Caderno universitário",
                "Caderno universitário de 10 matérias.",
                foto("📓", "#5c8dd6"), 80));
    }

    // Imagem SVG embutida, para a demonstração não depender de sites externos
    private static String foto(String emoji, String cor) {
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='600' height='300'>"
                + "<rect width='600' height='300' fill='" + cor + "'/>"
                + "<text x='300' y='190' font-size='140' text-anchor='middle'>" + emoji + "</text></svg>";
        return "data:image/svg+xml;charset=utf-8," + URLEncoder.encode(svg, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void professor(String nome, String email, String cpf, String departamento, Instituicao instituicao) {
        Professor p = new Professor();
        p.setNome(nome);
        p.setEmail(email);
        p.setSenha(authService.hash(SENHA_PADRAO));
        p.setCpf(cpf);
        p.setDepartamento(departamento);
        p.setInstituicao(instituicao);
        professorRepository.save(p);
    }
}
