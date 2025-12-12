package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.services.GuildService;
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


@Path("/v1/guilds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Guilds (Servers)", description = "Manages Discord servers (Guilds) and their primary CRUD operations, including member, role, and channel relationships.")
@APIResponses(value = {
    @APIResponse(responseCode = "500", description = "Internal Server Error")
})
public class GuildResource {

    private final GuildService guildService;

    @Inject
    public GuildResource(GuildService guildService) {
        this.guildService = guildService;
    }

    @GET
    @Operation(summary = "Get all guilds", description = "Returns a complete list of all guilds.")
    @APIResponse(
        responseCode = "200",
        description = "List of all guilds",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = GuildDTO.class, type = SchemaType.ARRAY)
        )
    )
    public Response getAll() {
        return Response.ok(guildService.getAllGuilds()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get guild by ID", description = "Retrieves the detailed information for a specific guild using its Snowflake ID.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Guild found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild not found with this ID")
    })
    public Response getById(
        @Parameter(description = "Guild Snowflake ID", required = true, example = "111111111111111111")
        @PathParam("id") Long id
    ) {
        return guildService.getGuildById(id)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Operation(summary = "Create a new guild", description = "Creates a new guild. The ID must be supplied in the DTO.")
    @APIResponse(
        responseCode = "201",
        description = "Guild created successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
    )
    public Response create(
        @RequestBody(description = "DTO for guild creation (must include the Snowflake ID)", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildCreateDTO.class)))
        @Valid
        GuildCreateDTO dto
    ) {
        return Response.status(Response.Status.CREATED)
                .entity(guildService.createGuild(dto)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing guild", description = "Updates the name of a guild.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Guild updated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild not found")
    })
    public Response update(
        @Parameter(description = "Snowflake ID of the guild to update", required = true, example = "111111111111111111")
        @PathParam("id") Long id, 
        @RequestBody(description = "DTO for guild name update", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildUpdateDTO.class)))
        @Valid
        GuildUpdateDTO dto
    ) {
        try {
            return Response.ok(guildService.updateGuild(id, dto)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a guild", description = "Removes a guild and all its associated entities (users, roles, channels).")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Guild deleted successfully (No Content)"),
        @APIResponse(responseCode = "404", description = "Guild not found with this ID")
    })
    public Response delete(
        @Parameter(description = "Snowflake ID of the guild to delete", required = true, example = "111111111111111111")
        @PathParam("id") Long id
    ) {
        return guildService.deleteGuild(id) ? Response.noContent().build()
                                            : Response.status(Response.Status.NOT_FOUND).build();
    }

    // --- User Relations ---

    @PUT
    @Path("/{guildId}/users/{userId}")
    @Operation(summary = "Add user to guild", description = "Adds an existing user to the guild (bidirectional).")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "User added. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or User not found")
    })
    public Response addUser(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the user to add", required = true, example = "222222222222222222")
        @PathParam("userId") Long userId
    ) {
        try {
            return Response.ok(guildService.addUserToGuild(guildId, userId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{guildId}/users/{userId}")
    @Operation(summary = "Remove user from guild", description = "Removes a user from the guild (bidirectional).")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "User removed. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or User not found")
    })
    public Response removeUser(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the user to remove", required = true, example = "222222222222222222")
        @PathParam("userId") Long userId
    ) {
        try {
            return Response.ok(guildService.removeUserFromGuild(guildId, userId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // --- Role Relations ---

    @PUT
    @Path("/{guildId}/roles/{roleId}")
    @Operation(summary = "Add role to guild", description = "Associates an existing role with this guild (uni-directional to guild).")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Role added. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or Role not found")
    })
    public Response addRole(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the role to add", required = true, example = "333333333333333333")
        @PathParam("roleId") Long roleId
    ) {
        try {
            return Response.ok(guildService.addRoleToGuild(guildId, roleId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{guildId}/roles/{roleId}")
    @Operation(summary = "Remove role from guild", description = "Removes a role from the guild.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Role removed. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or Role not found")
    })
    public Response removeRole(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the role to remove", required = true, example = "333333333333333333")
        @PathParam("roleId") Long roleId
    ) {
        try {
            return Response.ok(guildService.removeRoleFromGuild(guildId, roleId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // --- Channel Relations ---

    @PUT
    @Path("/{guildId}/channels/{channelId}")
    @Operation(summary = "Add channel to guild", description = "Associates an existing channel with this guild (uni-directional to guild).")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Channel added. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or Channel not found")
    })
    public Response addChannel(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the channel to add", required = true, example = "444444444444444444")
        @PathParam("channelId") Long channelId
    ) {
        try {
            return Response.ok(guildService.addChannelToGuild(guildId, channelId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{guildId}/channels/{channelId}")
    @Operation(summary = "Remove channel from guild", description = "Removes a channel from the guild.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Channel removed. Returns the updated guild.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Guild or Channel not found")
    })
    public Response removeChannel(
        @Parameter(description = "Snowflake ID of the guild", required = true, example = "111111111111111111")
        @PathParam("guildId") Long guildId, 
        @Parameter(description = "Snowflake ID of the channel to remove", required = true, example = "444444444444444444")
        @PathParam("channelId") Long channelId
    ) {
        try {
            return Response.ok(guildService.removeChannelFromGuild(guildId, channelId)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}