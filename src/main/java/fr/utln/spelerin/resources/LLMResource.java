package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.llm.LLMRequest;
import fr.utln.spelerin.dto.llm.LLMResponse;
import fr.utln.spelerin.services.LLMService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.function.Supplier;

@Path("/v1/llm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "LLM Tools", description = "Outils IA avec gestion d'erreurs avancée")
public class LLMResource {

    private final LLMService llmService;

    @Inject
    public LLMResource(LLMService llmService) {
        this.llmService = llmService;
    }

    @POST
    @Path("/teach")
    @Operation(summary = "Expliquer un concept")
    public Response teach(@Valid LLMRequest request) {
        return handleLlmCall(() -> llmService.teach(request.prompt()));
    }

    @POST
    @Path("/translate")
    @Operation(summary = "Traduire un texte")
    public Response translate(@Valid LLMRequest request) {
        return handleLlmCall(() -> llmService.translate(request.prompt()));
    }

    @POST
    @Path("/summarize")
    @Operation(summary = "Résumer un contenu")
    public Response summarize(@Valid LLMRequest request) {
        return handleLlmCall(() -> llmService.summarize(request.prompt()));
    }

    /**
     * Gestion centralisée des appels bloquants et des erreurs.
     */
    private Response handleLlmCall(Supplier<String> llmAction) {
        try {
            // L'appel bloquant se fait ici. Quarkus gère le thread worker automatiquement.
            String answer = llmAction.get();
            return Response.ok(new LLMResponse(answer)).build();

        } catch (Exception e) {
            // Gestion des erreurs (Timeout, API OpenAI HS, etc.)
            // On pourrait affiner en catchant des exceptions spécifiques de LangChain4j
            return Response.serverError()
                    .entity(new LLMResponse("Erreur du service IA : " + e.getMessage()))
                    .build();
        }
    }
}