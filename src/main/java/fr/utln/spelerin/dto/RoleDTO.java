package fr.utln.spelerin.dto;

import java.util.Set;


public record RoleDTO(
	String id,
	String name,
	Long permissions,
	String guildId,
	Set<String> userIds,
	Set<String> accessibleChannelIds
){}