package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.Channel;
import jakarta.transaction.Transactional;
import fr.utln.spelerin.repositories.ChannelRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import fr.utln.spelerin.repositories.GuildRepository;

@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {
	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;

	@Inject
	public ChannelResource(ChannelRepository channelRepository, GuildRepository guildRepository){
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
	}

	@GET
	public List<Channel> getAllChannels() {
		return channelRepository.listAll();
	}

	@GET
	@Path("/{id}")
	public Response getChannelById(@PathParam("id") UUID id) {
		return channelRepository.findByIdOptional(id)
				.map(Response::ok)
				.orElse(Response.status(Response.Status.NOT_FOUND))
				.build();
	}

	@POST
	@Transactional
	public Response createChannel(Channel channel) {
		if (channel.getGuild() == null || channel.getGuild().getId() == null) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity("Guild ID is required").build();
		}

		return guildRepository.findByIdOptional(channel.getGuild().getId())
			.map(guild -> {
				channel.setGuild(guild);
				channelRepository.persist(channel);
				return Response.status(Response.Status.CREATED).entity(channel).build();
			})
			.orElse(Response.status(Response.Status.NOT_FOUND)
				.entity("Guild not found").build());
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response deleteChannel(@PathParam("id") UUID id) {
		boolean deleted = channelRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}