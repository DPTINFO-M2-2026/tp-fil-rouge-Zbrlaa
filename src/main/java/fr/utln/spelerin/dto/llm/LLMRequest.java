package fr.utln.spelerin.dto.llm;

import jakarta.validation.constraints.NotBlank;

public record LLMRequest(
	@NotBlank(message = "Le prompt est obligatoire et ne peut pas être vide.")
	String prompt
){}