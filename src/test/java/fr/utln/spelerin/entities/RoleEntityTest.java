package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest{
	private static final long ROLE_SNOWFLAKE = 666666666666666666L;
	private static final long USER_SNOWFLAKE = 777777777777777777L;
	private static final long CHANNEL_SNOWFLAKE = 888888888888888888L;

	@Test
	void addAndRemoveUserAndAccessibleChannel_shouldMaintainBidirectionalRelationsAndToStringIds() {
		Role role = Role.builder().name("roleA").permissions(4L).build();
		role.setId(ROLE_SNOWFLAKE);

		User user = User.builder().username("ru").displayName("RU").build();
		user.setId(USER_SNOWFLAKE);

		Channel channel = Channel.builder().name("chan").type(0).build();
		channel.setId(CHANNEL_SNOWFLAKE);

		// add user
		role.addUser(user);
		assertTrue(role.getUsers().contains(user));
		assertTrue(user.getRoles().contains(role));
		assertTrue(role.getUserIds().contains(USER_SNOWFLAKE));

		// remove user
		role.removeUser(user);
		assertFalse(role.getUsers().contains(user));
		assertFalse(user.getRoles().contains(role));

		// add accessible channel
		role.addAccessibleChannel(channel);
		assertTrue(role.getAccessibleChannels().contains(channel));
		assertTrue(channel.getRolesWithAccess().contains(role));
		assertTrue(role.getAccessibleChannelIds().contains(CHANNEL_SNOWFLAKE));

		// remove accessible channel
		role.removeAccessibleChannel(channel);
		assertFalse(role.getAccessibleChannels().contains(channel));
		assertFalse(channel.getRolesWithAccess().contains(role));
	}
}