package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.GuildDTO;
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

	// Snowflakes de test fixes pour la reproductibilité
	private static final Long GUILD_SNOWFLAKE = 555555555555555555L;
	private static final Long ROLE_SNOWFLAKE = 666666666666666666L;
	private static final Long USER_SNOWFLAKE = 777777777777777777L;
	private static final Long CHANNEL_SNOWFLAKE = 888888888888888888L;


	@Test
	void crud_and_relations_role() {
		// ---------- CREATE GUILD ----------
		GuildCreateDTO guildDto = new GuildCreateDTO(GUILD_SNOWFLAKE, "GuildTest");
		GuildDTO guild = given()
				.contentType(ContentType.JSON)
				.body(guildDto)
			.when()
				.post("/v1/guilds")
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
				.post("/v1/roles")
			.then()
				.statusCode(201)
				.extract().as(RoleDTO.class);
		assertEquals(ROLE_SNOWFLAKE, role.id());

		// ---------- CREATE USER ----------
		// Le DTO doit fournir le Snowflake
		UserCreateDTO userDto = new UserCreateDTO(USER_SNOWFLAKE, "user1","User One");
		UserDTO user = given()
				.contentType(ContentType.JSON)
				.body(userDto)
			.when()
				.post("/v1/users")
			.then()
				.statusCode(201)
				.extract().as(UserDTO.class);
		assertEquals(USER_SNOWFLAKE, user.id());

		// ---------- CREATE CHANNEL ----------
		// Le DTO doit fournir le Snowflake
		ChannelCreateDTO channelDto = new ChannelCreateDTO(CHANNEL_SNOWFLAKE, "chan1",0, guild.id());
		ChannelDTO channel = given()
				.contentType(ContentType.JSON)
				.body(channelDto)
			.when()
				.post("/v1/channels")
			.then()
				.statusCode(201)
				.extract().as(ChannelDTO.class);
		assertEquals(CHANNEL_SNOWFLAKE, channel.id());

		// ---------- ADD RELATIONS ----------
		given().when().put("/v1/roles/{roleId}/users/{userId}", role.id(), user.id())
				.then().statusCode(200);
		given().when().put("/v1/roles/{roleId}/channels/{channelId}", role.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS ----------
		RoleDTO roleWithRelations = given()
				.when().get("/v1/roles/{id}", role.id())
				.then().statusCode(200)
				.extract().as(RoleDTO.class);
		assertTrue(roleWithRelations.userIds().contains(user.id()));
		assertTrue(roleWithRelations.accessibleChannelIds().contains(channel.id()));

		// ---------- REMOVE RELATIONS ----------
		given().when().delete("/v1/roles/{roleId}/users/{userId}", role.id(), user.id())
				.then().statusCode(200);
		given().when().delete("/v1/roles/{roleId}/channels/{channelId}", role.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS REMOVED ----------
		RoleDTO updated = given()
				.when().get("/v1/roles/{id}", role.id())
				.then().statusCode(200)
				.extract().as(RoleDTO.class);
		assertFalse(updated.userIds().contains(user.id()));
		assertFalse(updated.accessibleChannelIds().contains(channel.id()));

		// ---------- DELETE ----------
		given().when().delete("/v1/roles/{id}", role.id())
				.then().statusCode(204);
		given().when().get("/v1/roles/{id}", role.id())
				.then().statusCode(404);
	}
}