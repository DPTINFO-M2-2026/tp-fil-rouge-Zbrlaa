package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.RoleDTO;
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

	// Snowflakes de test fixes pour la reproductibilité
	private static final String USER_SNOWFLAKE = "101010101010101010";
	private static final String GUILD_SNOWFLAKE = "202020202020202020";
	private static final String ROLE_SNOWFLAKE = "303030303030303030";

	@Test
	void crud_and_relations_user() {
		// ---------- CREATE USER ----------
		// Le DTO doit fournir le Snowflake
		UserCreateDTO userDto = new UserCreateDTO(USER_SNOWFLAKE, "user1","User One");
		UserDTO user = given()
				.contentType(ContentType.JSON)
				.body(userDto)
			.when()
				.post("/users")
			.then()
				.statusCode(201)
				.extract().as(UserDTO.class);
		assertEquals(USER_SNOWFLAKE, user.id());

		// ---------- CREATE GUILD ----------
		// Le DTO doit fournir le Snowflake
		GuildCreateDTO guildDto = new GuildCreateDTO(GUILD_SNOWFLAKE, "GuildTest");
		GuildDTO guild = given()
				.contentType(ContentType.JSON)
				.body(guildDto)
			.when()
				.post("/guilds")
			.then()
				.statusCode(201)
				.extract().as(GuildDTO.class);
		assertEquals(GUILD_SNOWFLAKE, guild.id());

		// ---------- CREATE ROLE ----------
		// Le DTO doit fournir le Snowflake
		RoleCreateDTO roleDto = new RoleCreateDTO(ROLE_SNOWFLAKE, "role1", 1L, guild.id());
		RoleDTO role = given()
				.contentType(ContentType.JSON)
				.body(roleDto)
			.when()
				.post("/roles")
			.then()
				.statusCode(201)
				.extract().as(RoleDTO.class);
		assertEquals(ROLE_SNOWFLAKE, role.id());

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