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
class ChannelResourceTest {

	@Test
	void createReadDeleteChannel() {
		// create guild first
		String guildId =
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"GuildForChannel\"}")
		.when().post("/guilds")
		.then()
			.statusCode(201)
			.extract().path("id");

		// CREATE CHANNEL with nested guild id
		String channelId =
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"MyChannel\",\"type\":\"text\",\"guild\":{\"id\":\"" + guildId + "\"}}")
		.when().post("/channels")
		.then()
			.statusCode(201)
			.body("name", is("MyChannel"))
			.body("id", notNullValue())
		.extract().path("id");

		// READ BY ID
		given()
		.when().get("/channels/" + channelId)
		.then()
			.statusCode(200)
			.body("name", is("MyChannel"))
			.body("guild.id", is(guildId));

		// DELETE
		given()
		.when().delete("/channels/" + channelId)
		.then()
			.statusCode(204);

		// VERIFY NOT FOUND
		given()
		.when().get("/channels/" + channelId)
		.then()
			.statusCode(404);
	}
}
