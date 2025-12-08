package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuildEntityTest{
	private static final long GUILD_SNOWFLAKE = 555555555555555555L;
	private static final long ROLE_SNOWFLAKE = 666666666666666666L;
	private static final long USER_SNOWFLAKE = 777777777777777777L;
	private static final long CHANNEL_SNOWFLAKE = 888888888888888888L;

	@Test
	void addAndRemoveUserRoleChannel_shouldMaintainBidirectionalRelationsAndToStringIds() {
		Guild guild = Guild.builder().name("MyGuild").build();
		guild.setId(GUILD_SNOWFLAKE);

		User user = User.builder().username("u").displayName("U").build();
		user.setId(USER_SNOWFLAKE);

		Role role = Role.builder().name("r").permissions(2L).build();
		role.setId(ROLE_SNOWFLAKE);

		Channel channel = Channel.builder().name("c").type(0).build();
		channel.setId(CHANNEL_SNOWFLAKE);

		// add user
		guild.addUser(user);
		assertTrue(guild.getUsers().contains(user));
		assertTrue(user.getGuilds().contains(guild));
		assertTrue(guild.getUserIds().contains(USER_SNOWFLAKE));

		// add role
		guild.addRole(role);
		assertTrue(guild.getRoles().contains(role));
		assertEquals(guild, role.getGuild());
		assertTrue(guild.getRoleIds().contains(ROLE_SNOWFLAKE));

		// add channel
		guild.addChannel(channel);
		assertTrue(guild.getChannels().contains(channel));
		assertEquals(guild, channel.getGuild());
		assertTrue(guild.getChannelIds().contains(CHANNEL_SNOWFLAKE));

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