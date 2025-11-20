package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.GuildCreateUpdateDTO;
import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class UserResourceTest {

	@Test
	void crud_and_relations_user() {
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
		assertNotNull(user.id());

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

		// ---------- ADD RELATIONS ----------
		given().when().put("/users/{userId}/guilds/{guildId}", user.id(), guild.id())
				.then().statusCode(200);
		given().when().put("/users/{userId}/roles/{roleId}", user.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS ----------
		UserDTO userWithRelations = given()
				.when().get("/users/{id}", user.id())
				.then().statusCode(200)
				.extract().as(UserDTO.class);
		assertTrue(userWithRelations.guildIds().contains(guild.id()));
		assertTrue(userWithRelations.roleIds().contains(role.id()));

		// ---------- REMOVE RELATIONS ----------
		given().when().delete("/users/{userId}/guilds/{guildId}", user.id(), guild.id())
				.then().statusCode(200);
		given().when().delete("/users/{userId}/roles/{roleId}", user.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS REMOVED ----------
		UserDTO updated = given()
				.when().get("/users/{id}", user.id())
				.then().statusCode(200)
				.extract().as(UserDTO.class);
		assertFalse(updated.guildIds().contains(guild.id()));
		assertFalse(updated.roleIds().contains(role.id()));

		// ---------- DELETE ----------
		given().when().delete("/users/{id}", user.id())
				.then().statusCode(204);
		given().when().get("/users/{id}", user.id())
				.then().statusCode(404);
	}
}