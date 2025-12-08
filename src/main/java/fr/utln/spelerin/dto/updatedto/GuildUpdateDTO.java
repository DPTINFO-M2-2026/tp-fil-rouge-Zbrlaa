package fr.utln.spelerin.dto.updatedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuildUpdateDTO(
	@NotBlank(message = "Le nom de la guilde est obligatoire.")
	@Size(min = 2, max = 100, message = "Le nom doit contenir entre {min} et {max} caractères.")
	String name
){}