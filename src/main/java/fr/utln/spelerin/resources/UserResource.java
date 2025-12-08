package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.services.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Manages Discord users and their relations with guilds and roles.")
@APIResponses(value = {
	@APIResponse(responseCode = "500", description = "Internal Server Error")
})
public class UserResource {

	private final UserService userService;

	@Inject
	public UserResource(UserService userService) {
		this.userService = userService;
	}

	@GET
	@Operation(summary = "Get all users", description = "Returns a complete list of all registered users.")
	@APIResponse(
		responseCode = "200",
		description = "List of all users",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON, 
			schema = @Schema(implementation = UserDTO.class, type = SchemaType.ARRAY)
		)
	)
	public Response getAll() {
		return Response.ok(userService.getAllUsers()).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get user by ID", description = "Retrieves the detailed information for a specific user using their Snowflake ID.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "User found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User not found with this ID")
	})
	public Response getById(
		@Parameter(description = "User Snowflake ID", required = true, example = "101010101010101010")
		@PathParam("id") Long id
	) {
		return userService.getUserById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Operation(summary = "Create a new user", description = "Creates a new user. The ID must be supplied in the DTO.")
	@APIResponse(
		responseCode = "201",
		description = "User created successfully",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
	)
	public Response create(
		@RequestBody(description = "DTO for user creation (must include the Snowflake ID)", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserCreateDTO.class)))
		@Valid
		UserCreateDTO dto
	) {
		UserDTO user = userService.createUser(dto);
		return Response.status(Response.Status.CREATED).entity(user).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing user", description = "Updates the user's username or display name.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "User updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User not found")
	})
	public Response update(
		@Parameter(description = "Snowflake ID of the user to update", required = true, example = "101010101010101010")
		@PathParam("id") Long id, 
		@RequestBody(description = "DTO for user update", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserUpdateDTO.class)))
		@Valid
		UserUpdateDTO dto
	) {
		try {
			return Response.ok(userService.updateUser(id, dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete a user", description = "Removes a user from the database.")
	@APIResponses(value = {
		@APIResponse(responseCode = "204", description = "User deleted successfully (No Content)"),
		@APIResponse(responseCode = "404", description = "User not found with this ID")
	})
	public Response delete(
		@Parameter(description = "Snowflake ID of the user to delete", required = true, example = "101010101010101010")
		@PathParam("id") Long id
	) {
		return userService.deleteUser(id) ? Response.noContent().build()
										: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Guild Relations ---

	@PUT
	@Path("/{userId}/guilds/{guildId}")
	@Operation(summary = "Add user to guild", description = "Adds an existing user to a guild (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Guild added to user. Returns the updated user.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User or Guild not found")
	})
	public Response addGuild(
		@Parameter(description = "Snowflake ID of the user", required = true, example = "101010101010101010")
		@PathParam("userId") Long userId, 
		@Parameter(description = "Snowflake ID of the guild to add", required = true, example = "202020202020202020")
		@PathParam("guildId") Long guildId
	) {
		try {
			return Response.ok(userService.addGuildToUser(userId, guildId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{userId}/guilds/{guildId}")
	@Operation(summary = "Remove user from guild", description = "Removes an existing user from a guild (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Guild removed from user. Returns the updated user.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User or Guild not found")
	})
	public Response removeGuild(
		@Parameter(description = "Snowflake ID of the user", required = true, example = "101010101010101010")
		@PathParam("userId") Long userId, 
		@Parameter(description = "Snowflake ID of the guild to remove", required = true, example = "202020202020202020")
		@PathParam("guildId") Long guildId
	) {
		try {
			return Response.ok(userService.removeGuildFromUser(userId, guildId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	// --- Role Relations ---

	@PUT
	@Path("/{userId}/roles/{roleId}")
	@Operation(summary = "Add role to user", description = "Assigns an existing role to a user (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role added to user. Returns the updated user.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User or Role not found")
	})
	public Response addRole(
		@Parameter(description = "Snowflake ID of the user", required = true, example = "101010101010101010")
		@PathParam("userId") Long userId, 
		@Parameter(description = "Snowflake ID of the role to add", required = true, example = "303030303030303030")
		@PathParam("roleId") Long roleId
	) {
		try {
			return Response.ok(userService.addRoleToUser(userId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{userId}/roles/{roleId}")
	@Operation(summary = "Remove role from user", description = "Removes an existing role from a user (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role removed from user. Returns the updated user.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))
		),
		@APIResponse(responseCode = "404", description = "User or Role not found")
	})
	public Response removeRole(
		@Parameter(description = "Snowflake ID of the user", required = true, example = "101010101010101010")
		@PathParam("userId") Long userId, 
		@Parameter(description = "Snowflake ID of the role to remove", required = true, example = "303030303030303030")
		@PathParam("roleId") Long roleId
	) {
		try {
			return Response.ok(userService.removeRoleFromUser(userId, roleId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}