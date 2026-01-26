package fr.utln.spelerin.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;


public record ChannelDTO(
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long id,
	String name,
	int type,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long guildId,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> rolesWithAccessIds
){}