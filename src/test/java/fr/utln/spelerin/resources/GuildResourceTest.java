package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class GuildResourceTest {

	// Snowflakes de test fixes pour la reproductibilité
	private static final String GUILD_SNOWFLAKE = "111111111111111111";
	private static final String USER_SNOWFLAKE = "222222222222222222";
	private static final String ROLE_SNOWFLAKE = "333333333333333333";
	private static final String CHANNEL_SNOWFLAKE = "444444444444444444";


	@Test
	void crud_and_relations_guild() {
		// ---------- CREATE GUILD ----------
		// Le DTO doit fournir le Snowflake
		GuildCreateDTO guildDto = new GuildCreateDTO(GUILD_SNOWFLAKE, "TestGuild");
		GuildDTO guild = given()
				.contentType(ContentType.JSON)
				.body(guildDto)
			.when()
				.post("/guilds")
			.then()
				.statusCode(201)
				.extract().as(GuildDTO.class);

		assertEquals(GUILD_SNOWFLAKE, guild.id());

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

		// ---------- CREATE CHANNEL ----------
		// Le DTO doit fournir le Snowflake
		ChannelCreateDTO channelDto = new ChannelCreateDTO(CHANNEL_SNOWFLAKE, "chan1","text", guild.id());
		ChannelDTO channel = given()
				.contentType(ContentType.JSON)
				.body(channelDto)
			.when()
				.post("/channels")
			.then()
				.statusCode(201)
				.extract().as(ChannelDTO.class);
		
		assertEquals(CHANNEL_SNOWFLAKE, channel.id());

		// ---------- ADD RELATIONS ----------
		given().when().put("/guilds/{guildId}/users/{userId}", guild.id(), user.id())
				.then().statusCode(200);
		given().when().put("/guilds/{guildId}/roles/{roleId}", guild.id(), role.id())
				.then().statusCode(200);
		given().when().put("/guilds/{guildId}/channels/{channelId}", guild.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS ----------
		GuildDTO guildWithRelations = given()
				.when().get("/guilds/{id}", guild.id())
				.then().statusCode(200)
				.extract().as(GuildDTO.class);

		assertTrue(guildWithRelations.userIds().contains(user.id()));
		assertTrue(guildWithRelations.roleIds().contains(role.id()));
		assertTrue(guildWithRelations.channelIds().contains(channel.id()));

		// ---------- REMOVE RELATIONS ----------
		given().when().delete("/guilds/{guildId}/users/{userId}", guild.id(), user.id())
				.then().statusCode(200);
		given().when().delete("/guilds/{guildId}/roles/{roleId}", guild.id(), role.id())
				.then().statusCode(200);
		given().when().delete("/guilds/{guildId}/channels/{channelId}", guild.id(), channel.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATIONS REMOVED ----------
		GuildDTO updated = given()
				.when().get("/guilds/{id}", guild.id())
				.then().statusCode(200)
				.extract().as(GuildDTO.class);

		assertFalse(updated.userIds().contains(user.id()));
		assertFalse(updated.roleIds().contains(role.id()));
		assertFalse(updated.channelIds().contains(channel.id()));

		// ---------- DELETE ----------
		given().when().delete("/guilds/{id}", guild.id())
				.then().statusCode(204);

		given().when().get("/guilds/{id}", guild.id())
				.then().statusCode(404);
	}
}