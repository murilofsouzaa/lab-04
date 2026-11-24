package br.pucminas.moeda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FluxosPrincipaisTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode login(String email) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(body);
    }

    @Test
    void rotasProtegidasExigemLogin() throws Exception {
        mvc.perform(get("/api/vantagens")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/instituicoes")).andExpect(status().isOk());
    }

    @Test
    void professorEnviaMoedasEAlunoResgataVantagem() throws Exception {
        JsonNode prof = login("ana.souza@pucminas.br");
        JsonNode aluno = login("joao@aluno.com");
        String tp = "Bearer " + prof.get("token").asText();
        String ta = "Bearer " + aluno.get("token").asText();
        long idProf = prof.get("id").asLong();
        long idAluno = aluno.get("id").asLong();

        mvc.perform(get("/api/professores/" + idProf + "/extrato").header("Authorization", tp))
                .andExpect(jsonPath("$.saldo").value(1000));

        // motivo é obrigatório
        mvc.perform(post("/api/professores/" + idProf + "/enviar-moedas").header("Authorization", tp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idAluno\":" + idAluno + ",\"valor\":100,\"motivo\":\" \"}"))
                .andExpect(status().isBadRequest());

        // saldo insuficiente
        mvc.perform(post("/api/professores/" + idProf + "/enviar-moedas").header("Authorization", tp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idAluno\":" + idAluno + ",\"valor\":5000,\"motivo\":\"x\"}"))
                .andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/professores/" + idProf + "/enviar-moedas").header("Authorization", tp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idAluno\":" + idAluno + ",\"valor\":100,\"motivo\":\"Participação em aula\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ENVIO"));

        mvc.perform(get("/api/professores/" + idProf + "/extrato").header("Authorization", tp))
                .andExpect(jsonPath("$.saldo").value(900));

        // professor não pode agir em nome de outro usuário
        mvc.perform(get("/api/alunos/" + idAluno + "/extrato").header("Authorization", tp))
                .andExpect(status().isForbidden());

        String vantagens = mvc.perform(get("/api/vantagens").header("Authorization", ta))
                .andReturn().getResponse().getContentAsString();
        long idVantagem = json.readTree(vantagens).get(0).get("id").asLong();
        int custo = json.readTree(vantagens).get(0).get("custo").asInt();

        mvc.perform(post("/api/alunos/" + idAluno + "/resgatar-vantagem/" + idVantagem).header("Authorization", ta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoCupom").isNotEmpty());

        mvc.perform(get("/api/alunos/" + idAluno + "/extrato").header("Authorization", ta))
                .andExpect(jsonPath("$.saldo").value(100 - custo))
                .andExpect(jsonPath("$.transacoes.length()").value(2));
    }
}
