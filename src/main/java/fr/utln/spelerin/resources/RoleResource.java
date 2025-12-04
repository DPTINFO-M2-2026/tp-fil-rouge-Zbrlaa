package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.updatedto.RoleUpdateDTO;
import fr.utln.spelerin.services.RoleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.NoSuchElementException;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {

	private final RoleService roleService;

	@Inject
	public RoleResource(RoleService roleService) {
		this.roleService = roleService;
	}

	@GET
	public Response getAll() {
		return Response.ok(roleService.getAllRoles()).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") String id) {
		return roleService.getRoleById(id)
				.map(dto -> Response.ok(dto).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	public Response create(RoleCreateDTO dto) {
		try {
			return Response.status(Response.Status.CREATED)
					.entity(roleService.createRole(dto)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String id, RoleUpdateDTO dto) {
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
	public Response delete(@PathParam("id") String id) {
		return roleService.deleteRole(id) ? Response.noContent().build()
										: Response.status(Response.Status.NOT_FOUND).build();
	}

	@PUT
	@Path("/{roleId}/users/{userId}")
	public Response addUser(@PathParam("roleId") String roleId, @PathParam("userId") String userId) {
		try {
			return Response.ok(roleService.addUserToRole(roleId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{roleId}/users/{userId}")
	public Response removeUser(@PathParam("roleId") String roleId, @PathParam("userId") String userId) {
		try {
			return Response.ok(roleService.removeUserFromRole(roleId, userId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/{roleId}/channels/{channelId}")
	public Response addChannel(@PathParam("roleId") String roleId, @PathParam("channelId") String channelId) {
		try {
			return Response.ok(roleService.addChannelToRole(roleId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{roleId}/channels/{channelId}")
	public Response removeChannel(@PathParam("roleId") String roleId, @PathParam("channelId") String channelId) {
		try {
			return Response.ok(roleService.removeChannelFromRole(roleId, channelId)).build();
		} catch (NoSuchElementException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}