package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest{
	private static final long GUILD_SNOWFLAKE = 555555555555555555L;
	private static final long ROLE_SNOWFLAKE = 666666666666666666L;
	private static final long USER_SNOWFLAKE = 777777777777777777L;
	
	@Test
	void addAndRemoveGuildAndRole_shouldMaintainBidirectionalRelationsAndToStringIds(){
		User user = User.builder()
				.username("testuser")
				.displayName("Test User")
				.build();
		user.setId(USER_SNOWFLAKE);

		Guild guild = Guild.builder().name("G1").build();
		guild.setId(GUILD_SNOWFLAKE);

		Role role = Role.builder().name("R1").permissions(1L).build();
		role.setId(ROLE_SNOWFLAKE);

		// add guild via user helper
		user.addGuild(guild);
		assertTrue(user.getGuilds().contains(guild));
		assertTrue(guild.getUsers().contains(user));
		assertTrue(user.getGuildIds().contains(GUILD_SNOWFLAKE));

		// remove guild
		user.removeGuild(guild);
		assertFalse(user.getGuilds().contains(guild));
		assertFalse(guild.getUsers().contains(user));

		// add role via user helper
		user.addRole(role);
		assertTrue(user.getRoles().contains(role));
		assertTrue(role.getUsers().contains(user));
		assertTrue(user.getRoleIds().contains(ROLE_SNOWFLAKE));

		// remove role
		user.removeRole(role);
		assertFalse(user.getRoles().contains(role));
		assertFalse(role.getUsers().contains(user));
	}
}