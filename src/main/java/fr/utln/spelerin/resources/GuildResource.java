package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.services.GuildService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

@Path("/guilds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuildResource {

	private final GuildService guildService;

	@Inject
	public GuildResource(GuildService guildService) {
		this.guildService = guildService;
	}

	@GET
	public Response getAll() {
		return Response.ok(guildService.getAllGuilds()).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") String id) {
		return guildService.getGuildById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	public Response create(GuildCreateDTO dto) {
		return Response.status(Response.Status.CREATED)
				.entity(guildService.createGuild(dto)).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String id, GuildUpdateDTO dto) {
		try {
			return Response.ok(guildService.updateGuild(id, dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		return guildService.deleteGuild(id) ? Response.noContent().build()
											: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations User ---

	@PUT
	@Path("/{guildId}/users/{userId}")
	public Response addUser(@PathParam("guildId") String guildId, @PathParam("userId") String userId) {
		try {
			return Response.ok(guildService.addUserToGuild(guildId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{guildId}/users/{userId}")
	public Response removeUser(@PathParam("guildId") String guildId, @PathParam("userId") String userId) {
		try {
			return Response.ok(guildService.removeUserFromGuild(guildId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	// --- Relations Role ---

	@PUT
	@Path("/{guildId}/roles/{roleId}")
	public Response addRole(@PathParam("guildId") String guildId, @PathParam("roleId") String roleId) {
		try {
			return Response.ok(guildService.addRoleToGuild(guildId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{guildId}/roles/{roleId}")
	public Response removeRole(@PathParam("guildId") String guildId, @PathParam("roleId") String roleId) {
		try {
			return Response.ok(guildService.removeRoleFromGuild(guildId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	// --- Relations Channel ---

	@PUT
	@Path("/{guildId}/channels/{channelId}")
	public Response addChannel(@PathParam("guildId") String guildId, @PathParam("channelId") String channelId) {
		try {
			return Response.ok(guildService.addChannelToGuild(guildId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{guildId}/channels/{channelId}")
	public Response removeChannel(@PathParam("guildId") String guildId, @PathParam("channelId") String channelId) {
		try {
			return Response.ok(guildService.removeChannelFromGuild(guildId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}