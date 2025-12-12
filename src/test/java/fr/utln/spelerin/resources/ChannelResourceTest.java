package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
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
class ChannelResourceTest {
	
	// Snowflakes de test fixes pour la reproductibilité
	private static final Long GUILD_SNOWFLAKE = 100000000000000001L;
	private static final Long CHANNEL_SNOWFLAKE = 200000000000000002L;
	private static final Long ROLE_SNOWFLAKE = 300000000000000003L;

	@Test
	void crud_and_relations_channel() {
		// ---------- CREATE GUILD ----------
		// Le DTO prend maintenant l'ID Snowflake en argument
		GuildCreateDTO guildDto = new GuildCreateDTO(GUILD_SNOWFLAKE, "GuildTest"); 
		GuildDTO guild = given()
				.contentType(ContentType.JSON)
				.body(guildDto)
			.when()
				.post("/v1/guilds")
			.then()
				.statusCode(201)
				.extract().as(GuildDTO.class);
		
		// Vérification de l'ID fourni
		assertEquals(GUILD_SNOWFLAKE, guild.id());

		// ---------- CREATE CHANNEL ----------
		// Le DTO prend maintenant l'ID Snowflake du channel en premier argument
		ChannelCreateDTO channelDto = new ChannelCreateDTO(
			CHANNEL_SNOWFLAKE,
			"chan1",
			0,
			guild.id()
		);
		ChannelDTO channel = given()
				.contentType(ContentType.JSON)
				.body(channelDto)
			.when()
				.post("/v1/channels")
			.then()
				.statusCode(201)
				.extract().as(ChannelDTO.class);
		
		// Vérification de l'ID fourni
		assertEquals(CHANNEL_SNOWFLAKE, channel.id());

		// ---------- CREATE ROLE ----------
		// Le DTO prend maintenant l'ID Snowflake du rôle en premier argument
		RoleCreateDTO roleDto = new RoleCreateDTO(ROLE_SNOWFLAKE, "role1", 1L, guild.id());
		RoleDTO role = given()
				.contentType(ContentType.JSON)
				.body(roleDto)
			.when()
				.post("/v1/roles")
			.then()
				.statusCode(201)
				.extract().as(RoleDTO.class);
		
		// Vérification de l'ID fourni
		assertEquals(ROLE_SNOWFLAKE, role.id());

		// ---------- ADD RELATION ----------
		given().when().put("/v1/channels/{channelId}/roles/{roleId}", channel.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATION ----------
		ChannelDTO channelWithRelations = given()
				.when().get("/v1/channels/{id}", channel.id())
				.then().statusCode(200)
				.extract().as(ChannelDTO.class);
		assertTrue(channelWithRelations.rolesWithAccessIds().contains(role.id()));

		// ---------- REMOVE RELATION ----------
		given().when().delete("/v1/channels/{channelId}/roles/{roleId}", channel.id(), role.id())
				.then().statusCode(200);

		// ---------- VERIFY RELATION REMOVED ----------
		ChannelDTO updated = given()
				.when().get("/v1/channels/{id}", channel.id())
				.then().statusCode(200)
				.extract().as(ChannelDTO.class);
		assertFalse(updated.rolesWithAccessIds().contains(role.id()));

		// ---------- DELETE ----------
		given().when().delete("/v1/channels/{id}", channel.id())
				.then().statusCode(204);
		given().when().get("/v1/channels/{id}", channel.id())
				.then().statusCode(404);
	}
}