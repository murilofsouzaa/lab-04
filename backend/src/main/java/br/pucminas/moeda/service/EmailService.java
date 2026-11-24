package br.pucminas.moeda.service;

import br.pucminas.moeda.model.Aluno;
import br.pucminas.moeda.model.EmpresaParceira;
import br.pucminas.moeda.model.Professor;
import br.pucminas.moeda.model.Vantagem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String host;
    private final String remetente;

    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${spring.mail.host:}") String host,
                        @Value("${app.mail.from}") String remetente) {
        this.mailSender = mailSender;
        this.host = host;
        this.remetente = remetente;
    }

    public void enviarEmailRecebimentoMoedas(Aluno aluno, Professor professor, int valor, String motivo) {
        enviar(aluno.getEmail(), "Você recebeu " + valor + " moedas!",
                "Olá, " + aluno.getNome() + "!\n\n"
                        + "O(a) professor(a) " + professor.getNome() + " enviou " + valor + " moedas para você.\n"
                        + "Motivo: " + motivo + "\n\nSaldo atual: " + aluno.getSaldo() + " moedas.");
    }

    public void enviarEmailCupomAluno(Aluno aluno, Vantagem vantagem, String codigo) {
        enviar(aluno.getEmail(), "Seu cupom: " + vantagem.getNome(),
                "Olá, " + aluno.getNome() + "!\n\n"
                        + "Você resgatou \"" + vantagem.getNome() + "\" de " + vantagem.getEmpresa().getNome()
                        + " por " + vantagem.getCusto() + " moedas.\n"
                        + "Apresente o código abaixo na troca presencial:\n\n    " + codigo);
    }

    public void enviarEmailNotificacaoEmpresa(EmpresaParceira empresa, Aluno aluno, Vantagem vantagem, String codigo) {
        enviar(empresa.getEmail(), "Novo resgate: " + vantagem.getNome(),
                "O(a) aluno(a) " + aluno.getNome() + " resgatou \"" + vantagem.getNome() + "\".\n"
                        + "Confira o código na troca presencial:\n\n    " + codigo);
    }

    // Falha de e-mail não desfaz a transação de moedas: apenas registra no log
    private void enviar(String para, String assunto, String corpo) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (host == null || host.isBlank() || sender == null) {
            log.info("[E-MAIL simulado] Para: {} | Assunto: {}\n{}", para, assunto, corpo);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remetente);
            msg.setTo(para);
            msg.setSubject(assunto);
            msg.setText(corpo);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", para, e.getMessage());
        }
    }
}
