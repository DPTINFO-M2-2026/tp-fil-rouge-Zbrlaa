package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createupdatedto.ChannelCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.GuildCreateUpdateDTO;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class ChannelResourceTest {

	@Test
	void crud_and_relations_channel() {
		// ---------- CREATE GUILD ----------
		GuildCreateUpdateDTO guildDto = new GuildCreateUpdateDTO("GuildTest");
		GuildDTO guild = given()
				.contentType(ContentType.JSON)
				.body(guildDto)
			.when()
				.post("/guilds")
			.then()
				.statusCode(201)
				.extract().as(GuildDTO.class);

		// ---------- CREATE CHANNEL ----------
		ChannelCreateUpdateDTO channelDto = new ChannelCreateUpdateDTO("chan1","text", guild.id());
		ChannelDTO channel = given()
				.contentType(ContentType.JSON)
				.body(channelDto)
			.when()
				.post("/channels")
			.then()
				.statusCode(201)
				.extract().as(ChannelDTO.class);

		// ---------- CREATE ROLE ----------
		RoleCreateUpdateDTO roleDto = new RoleCreateUpdateDTO("role1", 1L, guild.id());
		RoleDTO role = given()
				.contentType(ContentType.JSON)
				.body(roleDto)
			.when()
				.post("/roles")
			.then()
				.statusCode(201)
				.extract().as(RoleDTO.class);

		// ---------- ADD RELATION ----------
		given().when().put("/channels/{channelId}/roles/{roleId}", channel.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATION ----------
		ChannelDTO channelWithRelations = given()
				.when().get("/channels/{id}", channel.id())
				.then().statusCode(200)
				.extract().as(ChannelDTO.class);
		assertTrue(channelWithRelations.rolesWithAccessIds().contains(role.id()));

		// ---------- REMOVE RELATION ----------
		given().when().delete("/channels/{channelId}/roles/{roleId}", channel.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATION REMOVED ----------
		ChannelDTO updated = given()
				.when().get("/channels/{id}", channel.id())
				.then().statusCode(200)
				.extract().as(ChannelDTO.class);
		assertFalse(updated.rolesWithAccessIds().contains(role.id()));

		// ---------- DELETE ----------
		given().when().delete("/channels/{id}", channel.id())
				.then().statusCode(204);
		given().when().get("/channels/{id}", channel.id())
				.then().statusCode(404);
	}
}