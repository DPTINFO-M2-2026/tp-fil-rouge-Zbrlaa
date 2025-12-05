package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.updatedto.RoleUpdateDTO;
import fr.utln.spelerin.services.RoleService;
import jakarta.inject.Inject;
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


@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Roles", description = "Manages Discord roles, permissions, and their relations with users and channels.")
@APIResponses(value = {
	@APIResponse(responseCode = "500", description = "Internal Server Error")
})
public class RoleResource {

	private final RoleService roleService;

	@Inject
	public RoleResource(RoleService roleService) {
		this.roleService = roleService;
	}

	@GET
	@Operation(summary = "Get all roles", description = "Returns a complete list of all roles across all guilds.")
	@APIResponse(
		responseCode = "200",
		description = "List of all roles",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON, 
			schema = @Schema(implementation = RoleDTO.class, type = SchemaType.ARRAY)
		)
	)
	public Response getAll() {
		return Response.ok(roleService.getAllRoles()).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get role by ID", description = "Retrieves the detailed information for a specific role using its Snowflake ID.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role not found with this ID")
	})
	public Response getById(
		@Parameter(description = "Role Snowflake ID", required = true, example = "666666666666666666")
		@PathParam("id") String id
	) {
		return roleService.getRoleById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Operation(summary = "Create a new role", description = "Creates a new role, associating it with an existing guild. The Role ID must be supplied in the DTO.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "201",
			description = "Role created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Guild specified by guildId not found")
	})
	public Response create(
		@RequestBody(description = "DTO for role creation (must include Role Snowflake ID and parent Guild ID)", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleCreateDTO.class)))
		RoleCreateDTO dto
	) {
		try {
			return Response.status(Response.Status.CREATED)
					.entity(roleService.createRole(dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing role", description = "Updates the role's name, permissions, or parent guild.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role not found"),
		@APIResponse(responseCode = "400", description = "Guild specified by guildId not found (IllegalArgumentException)")
	})
	public Response update(
		@Parameter(description = "Snowflake ID of the role to update", required = true, example = "666666666666666666")
		@PathParam("id") String id, 
		@RequestBody(description = "DTO for role update (must include the parent Guild ID)", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleUpdateDTO.class)))
		RoleUpdateDTO dto
	) {
		try {
			return Response.ok(roleService.updateRole(id, dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete a role", description = "Removes a role from the database.")
	@APIResponses(value = {
		@APIResponse(responseCode = "204", description = "Role deleted successfully (No Content)"),
		@APIResponse(responseCode = "404", description = "Role not found with this ID")
	})
	public Response delete(
		@Parameter(description = "Snowflake ID of the role to delete", required = true, example = "666666666666666666")
		@PathParam("id") String id
	) {
		return roleService.deleteRole(id) ? Response.noContent().build()
										: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- User Relations ---

	@PUT
	@Path("/{roleId}/users/{userId}")
	@Operation(summary = "Add user to role", description = "Assigns an existing user to this role (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "User added to role. Returns the updated role.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role or User not found")
	})
	public Response addUser(
		@Parameter(description = "Snowflake ID of the role", required = true, example = "666666666666666666")
		@PathParam("roleId") String roleId, 
		@Parameter(description = "Snowflake ID of the user to add", required = true, example = "777777777777777777")
		@PathParam("userId") String userId
	) {
		try {
			return Response.ok(roleService.addUserToRole(roleId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{roleId}/users/{userId}")
	@Operation(summary = "Remove user from role", description = "Removes an existing user from this role (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "User removed from role. Returns the updated role.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role or User not found")
	})
	public Response removeUser(
		@Parameter(description = "Snowflake ID of the role", required = true, example = "666666666666666666")
		@PathParam("roleId") String roleId, 
		@Parameter(description = "Snowflake ID of the user to remove", required = true, example = "777777777777777777")
		@PathParam("userId") String userId
	) {
		try {
			return Response.ok(roleService.removeUserFromRole(roleId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	// --- Channel Relations (Accessible Channels) ---

	@PUT
	@Path("/{roleId}/channels/{channelId}")
	@Operation(summary = "Grant channel access to role", description = "Grants this role access to a specific channel (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Access granted. Returns the updated role.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role or Channel not found")
	})
	public Response addChannel(
		@Parameter(description = "Snowflake ID of the role", required = true, example = "666666666666666666")
		@PathParam("roleId") String roleId, 
		@Parameter(description = "Snowflake ID of the channel to grant access to", required = true, example = "888888888888888888")
		@PathParam("channelId") String channelId
	) {
		try {
			return Response.ok(roleService.addChannelToRole(roleId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{roleId}/channels/{channelId}")
	@Operation(summary = "Revoke channel access from role", description = "Revokes this role's access from a specific channel (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Access revoked. Returns the updated role.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role or Channel not found")
	})
	public Response removeChannel(
		@Parameter(description = "Snowflake ID of the role", required = true, example = "666666666666666666")
		@PathParam("roleId") String roleId, 
		@Parameter(description = "Snowflake ID of the channel to remove access from", required = true, example = "888888888888888888")
		@PathParam("channelId") String channelId
	) {
		try {
			return Response.ok(roleService.removeChannelFromRole(roleId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}