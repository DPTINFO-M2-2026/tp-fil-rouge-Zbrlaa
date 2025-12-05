package fr.utln.spelerin.dto.createdto;


public record UserCreateDTO(
	long id,
	String username,
	String displayName
){}