package fr.utln.spelerin.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChannelEntityTest{
	@Test
	@DisplayName("Ajouter et retirer un rôle met à jour les relations et les IDs")
	void addRemoveRole(){
		Channel channel = Channel.builder().name("chan2").type("text").build();
		UUID channelId = UUID.randomUUID();
		channel.setId(channelId);

		Role role = Role.builder().name("r2").permissions(8L).build();
		UUID roleId = UUID.randomUUID();
		role.setId(roleId);

		// add role to channel
		channel.addRoleWithAccess(role);
		assertTrue(channel.getRolesWithAccess().contains(role));
		assertTrue(role.getAccessibleChannels().contains(channel));
		assertTrue(channel.getRoleIds().contains(roleId));

		// remove role
		channel.removeRoleWithAccess(role);
		assertFalse(channel.getRolesWithAccess().contains(role));
		assertFalse(role.getAccessibleChannels().contains(channel));
		assertFalse(channel.getRoleIds().contains(roleId));
	}
}