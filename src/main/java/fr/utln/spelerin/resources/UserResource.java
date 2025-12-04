package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserService userService;

	@Inject
	public UserResource(UserService userService) {
		this.userService = userService;
	}

	@GET
	public Response getAll() {
		return Response.ok(userService.getAllUsers()).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") String id) {
		return userService.getUserById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	public Response create(UserCreateDTO dto) {
		UserDTO user = userService.createUser(dto);
		return Response.status(Response.Status.CREATED).entity(user).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String id, UserUpdateDTO dto) {
		try {
			return Response.ok(userService.updateUser(id, dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		return userService.deleteUser(id) ? Response.noContent().build()
										: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations Guild ---

	@PUT
	@Path("/{userId}/guilds/{guildId}")
	public Response addGuild(@PathParam("userId") String userId, @PathParam("guildId") String guildId) {
		try {
			return Response.ok(userService.addGuildToUser(userId, guildId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{userId}/guilds/{guildId}")
	public Response removeGuild(@PathParam("userId") String userId, @PathParam("guildId") String guildId) {
		try {
			return Response.ok(userService.removeGuildFromUser(userId, guildId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	// --- Relations Role ---

	@PUT
	@Path("/{userId}/roles/{roleId}")
	public Response addRole(@PathParam("userId") String userId, @PathParam("roleId") String roleId) {
		try {
			return Response.ok(userService.addRoleToUser(userId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{userId}/roles/{roleId}")
	public Response removeRole(@PathParam("userId") String userId, @PathParam("roleId") String roleId) {
		try {
			return Response.ok(userService.removeRoleFromUser(userId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}