package fr.utln.spelerin.dto;

import java.util.Set;

public record InvitationDTO(
	long id,
	String discordCode,
	long guildId,
	Set<Long> roleIds
){}