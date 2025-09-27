package fr.utln.spelerin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;


@Startup
@ApplicationScoped
public class App{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private final UserRepository userRepository;

	@Inject
	public App(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public void onStart(@Observes StartupEvent ev) {
		logger.info("Lancement de l'application");

		User shawn = User.builder()
				.username("Zbrlaa")
				.displayName("Don Zbrlaa")
				.build();

		Guild skateClub = Guild.builder()
				.name("Skate Club")
				.build();

		// Relation bidirectionnelle
		shawn.getGuilds().add(skateClub);
		skateClub.getUsers().add(shawn);

		try {
			userRepository.persist(shawn);
			logger.info("Utilisateur {} persisté avec succès !", shawn.getUsername());
		} catch (Exception e) {
			logger.error("Erreur lors de la persistance de l'utilisateur", e);
		}

		User u = userRepository.findById(shawn.getId());
		if (u != null) {
			logger.info("Utilisateur {} retrouvé dans la base !", u.getUsername());
		} else {
			logger.warn("Utilisateur non trouvé dans la base !");
		}
	}
}