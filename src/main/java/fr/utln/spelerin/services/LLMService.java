package fr.utln.spelerin.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface LLMService {

    @SystemMessage("Tu es un expert technique pédagogue. Ton but est d'expliquer des concepts complexes de manière claire, concise et illustrée d'exemples si nécessaire.")
    @UserMessage("Explique le concept technique suivant : {concept}")
    String teach(String concept);

    @SystemMessage("Tu es un traducteur professionnel polyglotte. Traduis le texte fourni en français de manière naturelle et précise.")
    @UserMessage("Traduis le texte suivant : {text}")
    String translate(String text);

    @SystemMessage("Tu es un assistant efficace chargé de la synthèse. Résume le contenu fourni (texte ou discussion) en extrayant les points clés et les conclusions.")
    @UserMessage("Fais un résumé du contenu suivant : {content}")
    String summarize(String content);
}