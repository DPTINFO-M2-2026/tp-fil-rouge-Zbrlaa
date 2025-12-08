package fr.utln.spelerin.dto.createdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuildCreateDTO(
	@NotNull(message = "L'ID de la guilde est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	Long id,
	
	@NotBlank(message = "Le nom de la guilde est obligatoire.")
	@Size(min = 2, max = 100, message = "Le nom doit contenir entre {min} et {max} caractères.")
	String name
){}