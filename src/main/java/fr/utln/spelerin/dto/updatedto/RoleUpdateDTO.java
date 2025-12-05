package fr.utln.spelerin.dto.updatedto;

public record RoleUpdateDTO(
	String name,
	long permissions,
	long guildId
){}