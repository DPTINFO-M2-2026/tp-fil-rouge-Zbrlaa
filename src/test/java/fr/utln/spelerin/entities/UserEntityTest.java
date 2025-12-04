package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest{
	@Test
	void addAndRemoveGuildAndRole_shouldMaintainBidirectionalRelationsAndToStringIds(){
		User user = User.builder()
				.username("testuser")
				.displayName("Test User")
				.build();
		String userId = String.valueOf(System.nanoTime());
		user.setId(userId);

		Guild guild = Guild.builder().name("G1").build();
		String guildId = String.valueOf(System.nanoTime());
		guild.setId(guildId);

		Role role = Role.builder().name("R1").permissions(1L).build();
		String roleId = String.valueOf(System.nanoTime());
		role.setId(roleId);

		// add guild via user helper
		user.addGuild(guild);
		assertTrue(user.getGuilds().contains(guild));
		assertTrue(guild.getUsers().contains(user));
		assertTrue(user.getGuildIds().contains(guildId));

		// remove guild
		user.removeGuild(guild);
		assertFalse(user.getGuilds().contains(guild));
		assertFalse(guild.getUsers().contains(user));

		// add role via user helper
		user.addRole(role);
		assertTrue(user.getRoles().contains(role));
		assertTrue(role.getUsers().contains(user));
		assertTrue(user.getRoleIds().contains(roleId));

		// remove role
		user.removeRole(role);
		assertFalse(user.getRoles().contains(role));
		assertFalse(role.getUsers().contains(user));
	}
}