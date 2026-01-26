package fr.utln.spelerin.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

public record InvitationDTO(
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long id,
	String discordCode,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long guildId,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> roleIds
){}