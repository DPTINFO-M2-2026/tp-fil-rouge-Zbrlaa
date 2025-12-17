package fr.utln.spelerin.dto.createdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationCreateDTO(
	@NotBlank(message = "Le code discord est obligatoire.")
	String discordCode,
	
	@NotNull(message = "L'ID de la guilde est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	long guildId
){}