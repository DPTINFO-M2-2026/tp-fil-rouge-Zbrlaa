package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.services.ChannelService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {

	private final ChannelService channelService;

	@Inject
	public ChannelResource(ChannelService channelService) {
		this.channelService = channelService;
	}

	@GET
	public Response getAll() {
		return Response.ok(channelService.getAllChannels()).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") String id) {
		return channelService.getChannelById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	public Response create(ChannelCreateDTO dto) {
		try {
			ChannelDTO createdChannel = channelService.createChannel(dto);
			return Response.status(Response.Status.CREATED).entity(createdChannel).build();
		} catch (IllegalArgumentException e) {
			// Guild non trouvée => 400 Bad Request
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String id, ChannelUpdateDTO dto) {
		try {
			ChannelDTO updatedChannel = channelService.updateChannel(id, dto);
			return Response.ok(updatedChannel).build();
		} catch (NoSuchElementException e) {
			// Channel non trouvé => 404 Not Found
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			// Guild non trouvée => 400 Bad Request
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		boolean deleted = channelService.deleteChannel(id);
		return deleted ? Response.noContent().build()
					: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations Role ---

	@PUT
	@Path("/{channelId}/roles/{roleId}")
	public Response addRole(@PathParam("channelId") String channelId, @PathParam("roleId") String roleId) {
		try {
			ChannelDTO channel = channelService.addRoleToChannel(channelId, roleId);
			return Response.ok(channel).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{channelId}/roles/{roleId}")
	public Response removeRole(@PathParam("channelId") String channelId, @PathParam("roleId") String roleId) {
		try {
			ChannelDTO channel = channelService.removeRoleFromChannel(channelId, roleId);
			return Response.ok(channel).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}