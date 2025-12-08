package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.services.ChannelService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

// OPENAPI IMPORTS
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;


@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Channels", description = "Manages Discord channels (text, voice, etc.) and their role-based access permissions.")
@APIResponses(value = {
	@APIResponse(responseCode = "500", description = "Internal Server Error")
})
public class ChannelResource {

	private final ChannelService channelService;

	@Inject
	public ChannelResource(ChannelService channelService) {
		this.channelService = channelService;
	}

	@GET
	@Operation(summary = "Get all channels", description = "Returns a complete list of all channels across all guilds.")
	@APIResponse(
		responseCode = "200",
		description = "List of all channels",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class, type = SchemaType.ARRAY))
	)
	public Response getAll() {
		return Response.ok(channelService.getAllChannels()).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get channel by ID", description = "Retrieves the detailed information for a specific channel using its Snowflake ID.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Channel found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Channel not found with this ID")
	})
	public Response getById(
		@Parameter(description = "Channel Snowflake ID", required = true, example = "987654321098765432") 
		@PathParam("id") Long id
	) {
		return channelService.getChannelById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Operation(summary = "Create a new channel", description = "Creates a new channel and associates it with an existing guild. The ID must be supplied in the DTO.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "201",
			description = "Channel created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class))
		),
		@APIResponse(responseCode = "400", description = "Parent Guild not found (Invalid guildId) or invalid data")
	})
	public Response create(
		@RequestBody(description = "DTO for channel creation (must include the Snowflake ID)", required = true, 
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelCreateDTO.class)))
		@Valid
		ChannelCreateDTO dto
	) {
		try {
			ChannelDTO createdChannel = channelService.createChannel(dto);
			return Response.status(Response.Status.CREATED).entity(createdChannel).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing channel", description = "Updates the name, type, or parent guild of a channel.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Channel updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Channel not found"),
		@APIResponse(responseCode = "400", description = "New parent Guild not found (Invalid guildId)")
	})
	public Response update(
		@Parameter(description = "Snowflake ID of the channel to update", required = true, example = "987654321098765432")
		@PathParam("id") Long id, 
		@RequestBody(description = "DTO for channel update", required = true, 
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelUpdateDTO.class)))
		@Valid
		ChannelUpdateDTO dto
	) {
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
	@Operation(summary = "Delete a channel", description = "Removes a channel from the database.")
	@APIResponses(value = {
		@APIResponse(responseCode = "204", description = "Channel deleted successfully (No Content)"),
		@APIResponse(responseCode = "404", description = "Channel not found with this ID")
	})
	public Response delete(
		@Parameter(description = "Snowflake ID of the channel to delete", required = true, example = "987654321098765432")
		@PathParam("id") Long id
	) {
		boolean deleted = channelService.deleteChannel(id);
		return deleted ? Response.noContent().build()
					: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Role Relations ---

	@PUT
	@Path("/{channelId}/roles/{roleId}")
	@Operation(summary = "Add role access to channel", description = "Associates a role with this channel to grant access (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role added. Returns the updated channel.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Channel or Role not found")
	})
	public Response addRole(
		@Parameter(description = "Snowflake ID of the channel", required = true, example = "987654321098765432")
		@PathParam("channelId") Long channelId, 
		@Parameter(description = "Snowflake ID of the role to associate", required = true, example = "246813579024681357")
		@PathParam("roleId") Long roleId
	) {
		try {
			ChannelDTO channel = channelService.addRoleToChannel(channelId, roleId);
			return Response.ok(channel).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{channelId}/roles/{roleId}")
	@Operation(summary = "Remove role access from channel", description = "Disassociates a role from this channel, removing access (bidirectional).")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Role removed. Returns the updated channel.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChannelDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Channel or Role not found")
	})
	public Response removeRole(
		@Parameter(description = "Snowflake ID of the channel", required = true, example = "987654321098765432")
		@PathParam("channelId") Long channelId, 
		@Parameter(description = "Snowflake ID of the role to disassociate", required = true, example = "246813579024681357")
		@PathParam("roleId") Long roleId
	) {
		try {
			ChannelDTO channel = channelService.removeRoleFromChannel(channelId, roleId);
			return Response.ok(channel).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}