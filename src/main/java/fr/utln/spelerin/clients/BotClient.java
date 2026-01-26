package fr.utln.spelerin.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "bot-api")
@Path("/v1/invitations")
public interface BotClient {

	@GET
	@Path("/{guildId}")
	@Produces(MediaType.TEXT_PLAIN)
	String getInviteCode(@PathParam("guildId") long guildId);
}