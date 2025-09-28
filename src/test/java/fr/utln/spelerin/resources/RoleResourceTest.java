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
class RoleResourceTest {

	@Test
	void createReadDeleteRole() {
		// create guild first
		String guildId =
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"GuildForRole\"}")
		.when().post("/guilds")
		.then()
			.statusCode(201)
			.extract().path("id");

		// CREATE ROLE with nested guild id
		String roleId =
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"MyRole\",\"permissions\":7,\"guild\":{\"id\":\"" + guildId + "\"}}")
		.when().post("/roles")
		.then()
			.statusCode(201)
			.body("name", is("MyRole"))
			.body("permissions", is(7))
			.body("id", notNullValue())
		.extract().path("id");

		// READ BY ID
		given()
		.when().get("/roles/" + roleId)
		.then()
			.statusCode(200)
			.body("name", is("MyRole"))
			.body("guild.id", is(guildId));

		// DELETE
		given()
		.when().delete("/roles/" + roleId)
		.then()
			.statusCode(204);

		// VERIFY NOT FOUND
		given()
		.when().get("/roles/" + roleId)
		.then()
			.statusCode(404);
	}
}
