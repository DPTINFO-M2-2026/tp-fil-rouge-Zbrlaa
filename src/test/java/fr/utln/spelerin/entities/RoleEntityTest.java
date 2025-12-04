package fr.utln.spelerin.entities;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest{
	@Test
	void addAndRemoveUserAndAccessibleChannel_shouldMaintainBidirectionalRelationsAndToStringIds() {
		Role role = Role.builder().name("roleA").permissions(4L).build();
		String roleId = String.valueOf(System.nanoTime());
		role.setId(roleId);

		User user = User.builder().username("ru").displayName("RU").build();
		String userId = String.valueOf(System.nanoTime());
		user.setId(userId);

		Channel channel = Channel.builder().name("chan").type("voice").build();
		String channelId = String.valueOf(System.nanoTime());
		channel.setId(channelId);

		// add user
		role.addUser(user);
		assertTrue(role.getUsers().contains(user));
		assertTrue(user.getRoles().contains(role));
		assertTrue(role.getUserIds().contains(userId));

		// remove user
		role.removeUser(user);
		assertFalse(role.getUsers().contains(user));
		assertFalse(user.getRoles().contains(role));

		// add accessible channel
		role.addAccessibleChannel(channel);
		assertTrue(role.getAccessibleChannels().contains(channel));
		assertTrue(channel.getRolesWithAccess().contains(role));
		assertTrue(role.getAccessibleChannelIds().contains(channelId));

		// remove accessible channel
		role.removeAccessibleChannel(channel);
		assertFalse(role.getAccessibleChannels().contains(channel));
		assertFalse(channel.getRolesWithAccess().contains(role));
	}
}