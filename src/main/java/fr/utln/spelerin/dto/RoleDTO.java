package fr.utln.spelerin.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;


public record RoleDTO(
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long id,
	String name,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long permissions,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long guildId,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> userIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> accessibleChannelIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> invitationIds
){}