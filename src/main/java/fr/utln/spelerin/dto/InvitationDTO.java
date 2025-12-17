package fr.utln.spelerin.dto;


public record InvitationDTO(
	long id,
	String discordCode,
	long roleId,
	long guildId
){}