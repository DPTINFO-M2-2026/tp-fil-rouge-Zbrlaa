package fr.utln.spelerin.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChannelEntityTest{
	private static final long ROLE_SNOWFLAKE = 666666666666666666L;
	private static final long CHANNEL_SNOWFLAKE = 888888888888888888L;

	@Test
	@DisplayName("Ajouter et retirer un rôle met à jour les relations et les IDs")
	void addRemoveRole(){
		Channel channel = Channel.builder().name("chan2").type(0).build();
		channel.setId(CHANNEL_SNOWFLAKE);

		Role role = Role.builder().name("r2").permissions(8L).build();
		role.setId(ROLE_SNOWFLAKE);

		// add role to channel
		channel.addRoleWithAccess(role);
		assertTrue(channel.getRolesWithAccess().contains(role));
		assertTrue(role.getAccessibleChannels().contains(channel));
		assertTrue(channel.getRoleIds().contains(ROLE_SNOWFLAKE));

		// remove role
		channel.removeRoleWithAccess(role);
		assertFalse(channel.getRolesWithAccess().contains(role));
		assertFalse(role.getAccessibleChannels().contains(channel));
		assertFalse(channel.getRoleIds().contains(ROLE_SNOWFLAKE));
	}
}