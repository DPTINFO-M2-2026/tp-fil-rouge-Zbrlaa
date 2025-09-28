package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.Role;
import jakarta.transaction.Transactional;
import fr.utln.spelerin.repositories.RoleRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import fr.utln.spelerin.repositories.GuildRepository;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {
	private final RoleRepository roleRepository;
	private final GuildRepository guildRepository;

	@Inject
	public RoleResource(RoleRepository roleRepository, GuildRepository guildRepository){
		this.roleRepository = roleRepository;
		this.guildRepository = guildRepository;
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
	@Transactional
	public Response createRole(Role role) {
		if (role.getGuild() == null || role.getGuild().getId() == null) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity("Guild ID is required").build();
		}

		return guildRepository.findByIdOptional(role.getGuild().getId())
			.map(guild -> {
				// Force initialization of collections to avoid LazyInitializationException
				guild.getUserIds();
				guild.getRoleIds();
				guild.getChannelIds();
				role.setGuild(guild);
				roleRepository.persist(role);
				return Response.status(Response.Status.CREATED).entity(role).build();
			})
			.orElse(Response.status(Response.Status.NOT_FOUND)
				.entity("Guild not found").build());
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response deleteRole(@PathParam("id") UUID id) {
		boolean deleted = roleRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}