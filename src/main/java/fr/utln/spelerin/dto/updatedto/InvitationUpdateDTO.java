package fr.utln.spelerin.dto.updatedto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationUpdateDTO(
	@NotBlank(message = "Le code discord est obligatoire.")
	String discordCode,
	
	@NotNull(message = "L'ID du rôle est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	long roleId,
	
	@NotNull(message = "L'ID de la guilde est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	long guildId
){}