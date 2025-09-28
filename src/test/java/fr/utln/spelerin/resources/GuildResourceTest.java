package fr.utln.spelerin.resources;

import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class GuildResourceTest {

	@Test
	void createReadDeleteGuild() {
		// CREATE
		String guildId =
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"TestGuild\"}")
		.when().post("/guilds")
		.then()
			.statusCode(201)
			.body("name", is("TestGuild"))
			.body("id", notNullValue())
		.extract().path("id");

		// READ BY ID
		given()
		.when().get("/guilds/" + guildId)
		.then()
			.statusCode(200)
			.body("name", is("TestGuild"))
			.body("id", is(guildId));

		// DELETE
		given()
		.when().delete("/guilds/" + guildId)
		.then()
			.statusCode(204);

		// VERIFY NOT FOUND
		given()
		.when().get("/guilds/" + guildId)
		.then()
			.statusCode(404);
	}
}
