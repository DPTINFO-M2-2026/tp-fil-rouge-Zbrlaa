package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.repositories.GuildRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/guilds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuildResource {
	private final GuildRepository guildRepository;

	@Inject
	public GuildResource(GuildRepository guildRepository){
		this.guildRepository = guildRepository;
	}

	@GET
	public List<Guild> getAllGuilds() {
		return guildRepository.listAll();
	}

	@GET
	@Path("/{id}")
	public Response getGuildById(@PathParam("id") UUID id) {
		return guildRepository.findByIdOptional(id)
				.map(Response::ok)
				.orElse(Response.status(Response.Status.NOT_FOUND))
				.build();
	}

	@POST
	public Response createGuild(Guild guild) {
		guildRepository.persist(guild);
		return Response.status(Response.Status.CREATED).entity(guild).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteGuild(@PathParam("id") UUID id) {
		boolean deleted = guildRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}