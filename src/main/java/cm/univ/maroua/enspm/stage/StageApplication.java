package cm.univ.maroua.enspm.stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Point d'entree de l'application Spring Boot "stage".
 *
 * <p>L'application expose une API REST pour la gestion des stages,
 * l'authentification JWT, la planification des notifications et l'evaluation
 * publique des etudiants en entreprise.</p>
 */
@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class StageApplication extends SpringBootServletInitializer {

	/**
	 * Configure la source principale pour le deploiement en conteneur servlet (WAR).
	 *
	 * @param application builder Spring Boot
	 * @return builder configure avec la classe d'application principale
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StageApplication.class);
	}

	/**
	 * Lance l'application en mode autonome.
	 *
	 * @param args arguments de ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(StageApplication.class, args);
	}

}
