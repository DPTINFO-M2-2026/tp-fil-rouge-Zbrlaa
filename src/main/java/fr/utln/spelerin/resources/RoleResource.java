package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.repositories.RoleRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {
	private final RoleRepository roleRepository;

	@Inject
	public RoleResource(RoleRepository roleRepository){
		this.roleRepository = roleRepository;
	}

	@GET
	public List<Role> getAllRoles() {
		return roleRepository.listAll();
	}

	@GET
	@Path("/{id}")
	public Response getRoleById(@PathParam("id") UUID id) {
		return roleRepository.findByIdOptional(id)
				.map(Response::ok)
				.orElse(Response.status(Response.Status.NOT_FOUND))
				.build();
	}

	@POST
	public Response createRole(Role role) {
		roleRepository.persist(role);
		return Response.status(Response.Status.CREATED).entity(role).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteRole(@PathParam("id") UUID id) {
		boolean deleted = roleRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}