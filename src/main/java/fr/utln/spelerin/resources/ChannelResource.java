package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.repositories.ChannelRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {
	private final ChannelRepository channelRepository;

	@Inject
	public ChannelResource(ChannelRepository channelRepository){
		this.channelRepository = channelRepository;
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
	public Response createChannel(Channel channel) {
		channelRepository.persist(channel);
		return Response.status(Response.Status.CREATED).entity(channel).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteChannel(@PathParam("id") UUID id) {
		boolean deleted = channelRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}