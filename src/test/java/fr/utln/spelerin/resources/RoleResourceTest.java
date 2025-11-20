package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.ChannelCreateUpdateDTO;
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
class RoleResourceTest {

	@Test
	void crud_and_relations_role() {
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

		// ---------- CREATE USER ----------
		UserCreateUpdateDTO userDto = new UserCreateUpdateDTO("user1","User One");
		UserDTO user = given()
				.contentType(ContentType.JSON)
				.body(userDto)
			.when()
				.post("/users")
			.then()
				.statusCode(201)
				.extract().as(UserDTO.class);

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

		// ---------- ADD RELATIONS ----------
		given().when().put("/roles/{roleId}/users/{userId}", role.id(), user.id())
				.then().statusCode(200);
		given().when().put("/roles/{roleId}/channels/{channelId}", role.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS ----------
		RoleDTO roleWithRelations = given()
				.when().get("/roles/{id}", role.id())
				.then().statusCode(200)
				.extract().as(RoleDTO.class);
		assertTrue(roleWithRelations.userIds().contains(user.id()));
		assertTrue(roleWithRelations.accessibleChannelIds().contains(channel.id()));

		// ---------- REMOVE RELATIONS ----------
		given().when().delete("/roles/{roleId}/users/{userId}", role.id(), user.id())
				.then().statusCode(200);
		given().when().delete("/roles/{roleId}/channels/{channelId}", role.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS REMOVED ----------
		RoleDTO updated = given()
				.when().get("/roles/{id}", role.id())
				.then().statusCode(200)
				.extract().as(RoleDTO.class);
		assertFalse(updated.userIds().contains(user.id()));
		assertFalse(updated.accessibleChannelIds().contains(channel.id()));

		// ---------- DELETE ----------
		given().when().delete("/roles/{id}", role.id())
				.then().statusCode(204);
		given().when().get("/roles/{id}", role.id())
				.then().statusCode(404);
	}
}