package br.pucminas.moeda.config;

import br.pucminas.moeda.service.ProfessorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Credita 1.000 moedas aos professores no início de cada semestre (1º de janeiro e 1º de julho). */
@Component
public class SemestreScheduler {

    private final ProfessorService professorService;

    public SemestreScheduler(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @Scheduled(cron = "0 0 0 1 1,7 *", zone = "America/Sao_Paulo")
    public void creditarSemestre() {
        professorService.creditarSemestre();
    }
}
