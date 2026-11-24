# Sistema de Moeda Estudantil

Sistema para reconhecer o mérito estudantil com uma moeda virtual. Professores distribuem moedas aos alunos,
e os alunos trocam as moedas por vantagens oferecidas por empresas parceiras.

Laboratório 04 · Projeto de Software · PUC Minas. O enunciado está em [`docs/enunciado.pdf`](docs/enunciado.pdf).

## Funcionalidades

| Ator | O que pode fazer |
|---|---|
| **Aluno** | Cadastrar-se, editar e excluir a conta, ver vantagens, resgatar vantagem (recebe cupom por e-mail), consultar extrato (recebimentos e trocas) |
| **Professor** | Pré-cadastrado. Enviar moedas a alunos da sua instituição com motivo obrigatório, consultar extrato (envios e créditos semestrais) |
| **Empresa parceira** | Cadastrar-se, editar e excluir a conta, cadastrar e remover vantagens (nome, descrição, foto, custo), ver os resgates com o código do cupom |
| **Sistema** | Login e senha para todos, 1.000 moedas por semestre para cada professor (acumulável), e-mails de recebimento, de cupom ao aluno e de aviso à empresa |

## Tecnologias

- **Backend:** Java 21, Spring Boot 3.4 (Web, Data JPA, Validation, Mail), Hibernate. Banco H2 em arquivo localmente e PostgreSQL em produção.
- **Frontend:** React 19 + Vite. Sem biblioteca de UI e sem roteador: a tela muda conforme o tipo de usuário logado.
- **Deploy:** backend no Render (Docker + Postgres, ver [`render.yaml`](render.yaml)) e frontend na Vercel.

## Estrutura (MVC)

```
backend/src/main/java/br/pucminas/moeda/
├── model/        Entidades JPA (Model): Usuario, Aluno, Professor, EmpresaParceira, Instituicao, Vantagem, Transacao
├── repository/   Acesso a dados (Spring Data JPA, um repositório por entidade)
├── service/      Regras de negócio: AlunoService, ProfessorService, EmpresaService, VantagemService, ExtratoService, AuthService, EmailService
├── controller/   Endpoints REST (Controller)
├── dto/          Objetos de entrada e saída da API (records)
├── config/       Autenticação (interceptor), CORS, crédito semestral agendado e dados iniciais
└── exception/    Exceções de negócio e tratamento global de erros
frontend/src/     View: páginas por tipo de usuário (pages/) e componentes reutilizáveis (components/)
docs/diagramas/   Diagramas de sequência e de visão geral de interação (Lab04S01)
docs/acesso/      Gerador do PDF com o link do sistema e os logins
```

## Como rodar localmente

Requisitos: **JDK 21+**, **Maven 3.9+** e **Node 20+**.
Se o `java` padrão da máquina for mais antigo, aponte o Maven para o JDK 21, por exemplo:
`export JAVA_HOME=/usr/lib/jvm/java-21-openjdk`.

```bash
# 1) Backend: http://localhost:8080
cd backend
mvn spring-boot:run

# 2) Frontend: http://localhost:5173 (em outro terminal)
cd frontend
npm install
npm run dev
```

Se a porta 8080 estiver ocupada, use `PORT=8081 mvn spring-boot:run` e rode o frontend com
`VITE_API_URL=http://localhost:8081 npm run dev`. Se o frontend rodar em outra porta, ajuste `CORS_ORIGINS` no backend.

Testes automatizados do backend: `cd backend && mvn test`.

O banco local fica em `backend/data/`. Apague essa pasta para recriar os dados iniciais.
O console do H2 fica em `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/moeda`, usuário `sa`, sem senha).

## Usuários pré-cadastrados

A senha de todos é **`123456`**.

| Tipo | Login | Observação |
|---|---|---|
| Professor | `ana.souza@pucminas.br` | PUC Minas |
| Professor | `carlos.lima@pucminas.br` | PUC Minas |
| Professor | `beatriz.rocha@ufmg.br` | UFMG |
| Aluno | `joao@aluno.com` | PUC Minas |
| Aluno | `maria@aluno.com` | PUC Minas |
| Empresa | `cantina@empresa.com` | 2 vantagens |
| Empresa | `livraria@empresa.com` | 1 vantagem |

Instituições pré-cadastradas: PUC Minas e UFMG.

## API

Todas as rotas exigem o cabeçalho `Authorization: Bearer <token>`, exceto as marcadas como públicas.

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Login (público). Retorna `token`, `tipo`, `id` e `nome` |
| POST | `/api/auth/logout` | Invalida o token |
| GET | `/api/instituicoes` | Lista as instituições (público) |
| POST | `/api/alunos` | Cadastro de aluno (público) |
| GET | `/api/alunos` | Alunos da instituição do professor logado |
| GET / PUT / DELETE | `/api/alunos/{id}` | CRUD do próprio aluno |
| GET | `/api/alunos/{id}/extrato` | Saldo e transações do aluno |
| POST | `/api/alunos/{id}/resgatar-vantagem/{idVantagem}` | Resgata uma vantagem (Diagrama de Sequência B) |
| POST | `/api/professores/{id}/enviar-moedas` | Envia moedas `{idAluno, valor, motivo}` (Diagrama de Sequência A) |
| GET | `/api/professores/{id}/extrato` | Saldo e transações do professor |
| POST | `/api/empresas` | Cadastro de empresa (público) |
| GET / PUT / DELETE | `/api/empresas/{id}` | CRUD da própria empresa |
| GET / POST | `/api/empresas/{id}/vantagens` | Lista e cadastra as vantagens da empresa |
| DELETE | `/api/empresas/{id}/vantagens/{idVantagem}` | Remove uma vantagem |
| GET | `/api/empresas/{id}/extrato` | Resgates das vantagens da empresa |
| GET | `/api/vantagens` | Vantagens disponíveis para resgate |

Erros retornam `{"erro": "mensagem"}` com os status 400 (validação), 401 (sem login), 403 (recurso de outro usuário),
404 (não encontrado), 409 (CPF, CNPJ ou e-mail duplicado) e 422 (regra de negócio, como saldo insuficiente).

## Decisões de projeto

1. **Herança `Usuario`, com `Aluno`, `Professor` e `EmpresaParceira`** (JPA `JOINED`). O login é único para os três tipos,
   e o e-mail é o login, único no sistema inteiro. O tipo do usuário define a tela que o frontend mostra.
2. **Autenticação por token opaco**, sem Spring Security nem JWT. O login gera um UUID salvo no usuário.
   Um `HandlerInterceptor` valida o token e entrega o usuário logado aos controllers. As senhas são guardadas com BCrypt.
   Cada controller checa se o `{id}` da rota é do usuário logado (`AuthService.exigirDono`). Cada usuário tem uma sessão ativa por vez.
   Escolhemos o mais simples que atende "autenticação necessária para todos os requisitos".
3. **Saldo nas entidades `Aluno` e `Professor`**, com os métodos `creditar` e `debitar` (Information Expert, GRASP).
   `Transacao` registra o histórico com três tipos: `ENVIO`, `RESGATE` e `CREDITO_SEMESTRAL`. O extrato é montado a partir dela.
4. **Enviar moedas e resgatar vantagem são transacionais** (`@Transactional`). Débito, crédito e registro da transação
   são gravados juntos ou nenhum é gravado.
5. **Crédito semestral** com `@Scheduled` em 1º de janeiro e 1º de julho. Ele soma 1.000 moedas ao saldo atual, então o saldo acumula.
   Os professores pré-cadastrados recebem o primeiro crédito ao subir o sistema.
6. **O professor só envia moedas para alunos da sua instituição** ("seus alunos", no enunciado). A lista de alunos do professor também é filtrada assim.
7. **E-mails** (`EmailService`): usam SMTP se `MAIL_HOST` estiver configurado. Sem ele, o e-mail é apenas escrito no log
   (`[E-MAIL simulado]`), para o sistema rodar sem conta de e-mail. Uma falha de envio não desfaz a transação de moedas.
8. **O código do cupom** tem o formato `MOE-XXXXXXXX`. Ele é gerado no resgate, salvo na transação e enviado nos e-mails do aluno e da empresa.
   A empresa também vê o código na aba "Resgates" para conferir a troca.
9. **A foto da vantagem** é enviada pelo frontend como data URL (base64, até 500 KB) e salva no banco.
   Assim não é preciso um serviço de armazenamento de arquivos, que seria mais uma dependência no deploy gratuito.
10. **A remoção de vantagem é lógica** (`ativa = false`), para não quebrar extratos com resgates antigos.
    A exclusão de aluno ou empresa com transações é bloqueada (422) pelo mesmo motivo.
11. **Instituições e professores entram por seed** (`DataSeeder`), porque o enunciado diz que já são pré-cadastrados.
    Por isso não há telas para cadastrá-los. O seed roda apenas com o banco vazio.
12. **Banco:** H2 em arquivo no desenvolvimento, para rodar sem instalar nada. PostgreSQL em produção.
    O esquema é gerado pelo Hibernate (`ddl-auto=update`).

## Alinhamento com os diagramas

O código segue os diagramas de [`docs/diagramas/`](docs/diagramas/):

- **Sequência A, Enviar Moedas:** `ProfessorController.enviarMoedas` → `ProfessorService.enviarMoedas(idProfessor, dto)`
  → `findById` (professor e aluno) → `validaSaldo` → `atualizaSaldos` → `save(Transacao)`, `save(Professor)`, `save(Aluno)`
  → `EmailService.enviarEmailRecebimentoMoedas` → `201 Created (TransacaoResponseDTO)`.
- **Sequência B, Resgatar Vantagem:** `AlunoController.resgatarVantagem` → `AlunoService.resgatarVantagem(idAluno, idVantagem)`
  → `findById` (aluno e vantagem) → `validaSaldo` → `descontarSaldo` → `geraCodigoCupom` → `save(Transacao)`, `save(Aluno)`
  → `enviarEmailCupomAluno` e `enviarEmailNotificacaoEmpresa` → `201 Created`.
- **Visão geral de interação:** login → desvio por tipo de usuário → telas do professor (extrato e enviar moedas),
  do aluno (extrato e resgatar) e da empresa (extrato de resgates e cadastrar vantagem) → logout.

Diferenças que devem entrar na próxima versão dos diagramas:

- O diagrama mostra uma única lifeline "Repository (JPA)". No código há um repositório por entidade:
  `ProfessorRepository`, `AlunoRepository`, `VantagemRepository` e `TransacaoRepository`.
- Os caminhos de erro não aparecem nos diagramas. Faltam fragmentos `alt` para saldo insuficiente (422),
  aluno ou vantagem inexistente (404), aluno de outra instituição (422) e motivo vazio (400).

## Deploy (Lab04S02)

**Backend no Render:**

1. Suba o repositório no GitHub.
2. No Render, abra **New → Blueprint** e selecione o repositório. O [`render.yaml`](render.yaml) cria o serviço Docker e o Postgres gratuito.
3. Preencha `CORS_ORIGINS` com a URL do frontend na Vercel. Isso pode ser feito depois do passo do frontend.
4. Opcional: configure `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASSWORD` e `MAIL_FROM` para enviar e-mails de verdade (ex.: SMTP do Gmail com senha de app).

**Frontend na Vercel:**

1. Importe o repositório e defina o **Root Directory** como `frontend`. O framework Vite é detectado.
2. Defina a variável `VITE_API_URL` com a URL do backend no Render, por exemplo `https://moeda-estudantil-api.onrender.com`.
3. Faça o deploy. Depois volte ao Render e ajuste `CORS_ORIGINS` com a URL final da Vercel.

**PDF de acesso** (link do sistema e logins de todos os tipos de usuário):

```bash
./docs/acesso/gerar-pdf.sh https://SEU-FRONT.vercel.app https://SEU-BACK.onrender.com
# gera docs/acesso/acesso.pdf (requer Google Chrome ou Chromium)
```

## Variáveis de ambiente do backend

| Variável | Padrão | Uso |
|---|---|---|
| `PORT` | `8080` | Porta HTTP |
| `DATABASE_URL` | H2 em `./data/moeda` | URL JDBC. No Docker, é montada a partir de `DB_HOST`, `DB_PORT` e `DB_NAME` |
| `DB_USER` / `DB_PASSWORD` | `sa` / vazio | Credenciais do banco |
| `CORS_ORIGINS` | `http://localhost:5173` | Origens do frontend, separadas por vírgula |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASSWORD`, `MAIL_FROM` | vazio | SMTP. Sem `MAIL_HOST`, os e-mails vão só para o log |
