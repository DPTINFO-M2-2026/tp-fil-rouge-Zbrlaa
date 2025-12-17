package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.InvitationDTO;
import fr.utln.spelerin.dto.createdto.InvitationCreateDTO;
import fr.utln.spelerin.dto.updatedto.InvitationUpdateDTO;
import fr.utln.spelerin.services.InvitationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.NoSuchElementException;

@Path("/v1/invitations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Invitations", description = "Manages Discord invitations and their associations with roles and guilds.")
@APIResponses(value = {
	@APIResponse(responseCode = "500", description = "Internal Server Error")
})
public class InvitationResource {

	private final InvitationService invitationService;

	@Inject
	public InvitationResource(InvitationService invitationService) {
		this.invitationService = invitationService;
	}

	@GET
	@Operation(summary = "Get all invitations", description = "Returns a complete list of all active invitations.")
	@APIResponse(
		responseCode = "200",
		description = "List of all invitations",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON,
			schema = @Schema(implementation = InvitationDTO.class, type = SchemaType.ARRAY)
		)
	)
	public Response getAll() {
		return Response.ok(invitationService.getAllInvitations()).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get invitation by ID", description = "Retrieves detailed information for a specific invitation.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Invitation found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InvitationDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Invitation not found with this ID")
	})
	public Response getById(
		@Parameter(description = "Invitation ID", required = true, example = "1")
		@PathParam("id") Long id
	) {
		return invitationService.getInvitationById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Operation(summary = "Create a new invitation", description = "Creates an invitation linked to a specific role and guild.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "201",
			description = "Invitation created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InvitationDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Role or Guild not found")
	})
	public Response create(
		@RequestBody(description = "DTO for invitation creation", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InvitationCreateDTO.class)))
		@Valid InvitationCreateDTO dto
	) {
		try {
			return Response.status(Response.Status.CREATED)
					.entity(invitationService.createInvitation(dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing invitation", description = "Updates the invitation details or its relations.")
	@APIResponses(value = {
		@APIResponse(
			responseCode = "200",
			description = "Invitation updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InvitationDTO.class))
		),
		@APIResponse(responseCode = "404", description = "Invitation, Role or Guild not found")
	})
	public Response update(
		@Parameter(description = "ID of the invitation to update", required = true, example = "1")
		@PathParam("id") Long id,
		@RequestBody(description = "DTO for invitation update", required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InvitationUpdateDTO.class)))
		@Valid InvitationUpdateDTO dto
	) {
		try {
			return Response.ok(invitationService.updateInvitation(id, dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete an invitation", description = "Removes an invitation from the database.")
	@APIResponses(value = {
		@APIResponse(responseCode = "204", description = "Invitation deleted successfully"),
		@APIResponse(responseCode = "404", description = "Invitation not found")
	})
	public Response delete(
		@Parameter(description = "ID of the invitation to delete", required = true, example = "1")
		@PathParam("id") Long id
	) {
		return invitationService.deleteInvitation(id) ? Response.noContent().build()
				: Response.status(Response.Status.NOT_FOUND).build();
	}
}