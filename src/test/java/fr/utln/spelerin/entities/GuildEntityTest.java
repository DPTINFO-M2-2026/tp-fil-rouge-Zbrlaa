package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuildEntityTest{
	@Test
	void addAndRemoveUserRoleChannel_shouldMaintainBidirectionalRelationsAndToStringIds() {
		Guild guild = Guild.builder().name("MyGuild").build();
		String guildId = String.valueOf(System.nanoTime());
		guild.setId(guildId);

		User user = User.builder().username("u").displayName("U").build();
		String userId = String.valueOf(System.nanoTime());
		user.setId(userId);

		Role role = Role.builder().name("r").permissions(2L).build();
		String roleId = String.valueOf(System.nanoTime());
		role.setId(roleId);

		Channel channel = Channel.builder().name("c").type("text").build();
		String channelId = String.valueOf(System.nanoTime());
		channel.setId(channelId);

		// add user
		guild.addUser(user);
		assertTrue(guild.getUsers().contains(user));
		assertTrue(user.getGuilds().contains(guild));
		assertTrue(guild.getUserIds().contains(userId));

		// add role
		guild.addRole(role);
		assertTrue(guild.getRoles().contains(role));
		assertEquals(guild, role.getGuild());
		assertTrue(guild.getRoleIds().contains(roleId));

		// add channel
		guild.addChannel(channel);
		assertTrue(guild.getChannels().contains(channel));
		assertEquals(guild, channel.getGuild());
		assertTrue(guild.getChannelIds().contains(channelId));

		// remove operations
		guild.removeUser(user);
		assertFalse(guild.getUsers().contains(user));
		assertFalse(user.getGuilds().contains(guild));

		guild.removeRole(role);
		assertFalse(guild.getRoles().contains(role));
		assertNull(role.getGuild());

		guild.removeChannel(channel);
		assertFalse(guild.getChannels().contains(channel));
		assertNull(channel.getGuild());
	}
}