package fr.utln.spelerin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;


@Startup
@ApplicationScoped
public class App{
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	@Transactional
	public void onStart(@Observes StartupEvent ev) {
		logger.info("Lancement de l'application");
	}
}