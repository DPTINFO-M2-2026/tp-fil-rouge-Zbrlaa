package fr.utln.spelerin.tests;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

	private PostgreSQLContainer<?> postgres;

	@SuppressWarnings("resource")
	@Override
	public Map<String, String> start() {
		// image compatible et légère
		postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
				.withDatabaseName("test")
				.withUsername("test")
				.withPassword("test")
				.withInitScript("import.sql") // utilisation du script d'initialisation
		;
		postgres.start();

		Map<String, String> props = new HashMap<>();
		// Propriétés Quarkus attendues par l'application/tests
		props.put("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
		props.put("quarkus.datasource.username", postgres.getUsername());
		props.put("quarkus.datasource.password", postgres.getPassword());
		props.put("quarkus.datasource.db-kind", "postgresql");
		// Utiliser drop-and-create pour démarrer proprement dans le container éphémère
		props.put("quarkus.hibernate-orm.database.generation", "drop-and-create");
		// Optionnel: éviter le chargement de script import.sql de production pendant les tests
		props.put("quarkus.hibernate-orm.sql-load-script", "no-file");
		return props;
	}

	@Override
	public void stop() {
		if (postgres != null) {
			postgres.stop();
		}
	}
}
