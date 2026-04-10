package cm.univ.maroua.enspm.stage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class TestEmailService {

    @Autowired
    private EmailSenderService mailSender;

    @Test
    public void envoyerEmail() {
        mailSender.send("douwevincent@gmail.com", "Sujet de l'email", "Contenu d'un mail très compliqué");
        System.out.println("Email envoyé avec succès !");
    }
}
