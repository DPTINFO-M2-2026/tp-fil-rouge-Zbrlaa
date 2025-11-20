package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.UserMapper;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import fr.utln.spelerin.repositories.UserRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	@Inject
	public UserResource(GuildRepository guildRepository, RoleRepository roleRepository, UserRepository userRepository) {
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	// --- CRUD de base ---

	@GET
	public Response getAll() {
		return Response.ok(
				userRepository.listAll()
						.stream()
						.map(UserMapper::toDTO)
						.toList()
		).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") UUID id) {
		User user = userRepository.findById(id);
		if (user == null) return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(UserMapper.toDTO(user)).build();
	}

	@POST
	@Transactional
	public Response create(UserCreateUpdateDTO dto) {
		User user = UserMapper.toEntity(dto);
		userRepository.persist(user);
		return Response.status(Response.Status.CREATED).entity(UserMapper.toDTO(user)).build();
	}

	@PUT
	@Path("/{id}")
	@Transactional
	public Response update(@PathParam("id") UUID id, UserCreateUpdateDTO dto) {
		User user = userRepository.findById(id);
		if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

		UserMapper.updateEntity(user, dto);
		return Response.ok(UserMapper.toDTO(user)).build();
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response delete(@PathParam("id") UUID id) {
		boolean deleted = userRepository.deleteById(id);
		return deleted ? Response.noContent().build()
					: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations Guild ---

	@PUT
	@Path("/{userId}/guilds/{guildId}")
	@Transactional
	public Response addGuild(@PathParam("userId") UUID userId, @PathParam("guildId") UUID guildId) {
		User user = userRepository.findById(userId);
		Guild g = guildRepository.findById(guildId);
		if (user == null || g == null) return Response.status(Response.Status.NOT_FOUND).build();

		user.addGuild(g);
		return Response.ok(UserMapper.toDTO(user)).build();
	}

	@DELETE
	@Path("/{userId}/guilds/{guildId}")
	@Transactional
	public Response removeGuild(@PathParam("userId") UUID userId, @PathParam("guildId") UUID guildId) {
		User user = userRepository.findById(userId);
		Guild g = guildRepository.findById(guildId);
		if (user == null || g == null) return Response.status(Response.Status.NOT_FOUND).build();

		user.removeGuild(g);
		return Response.ok(UserMapper.toDTO(user)).build();
	}

	// --- Relations Role ---

	@PUT
	@Path("/{userId}/roles/{roleId}")
	@Transactional
	public Response addRole(@PathParam("userId") UUID userId, @PathParam("roleId") UUID roleId) {
		User user = userRepository.findById(userId);
		Role r = roleRepository.findById(roleId);
		if (user == null || r == null) return Response.status(Response.Status.NOT_FOUND).build();

		user.addRole(r);
		return Response.ok(UserMapper.toDTO(user)).build();
	}

	@DELETE
	@Path("/{userId}/roles/{roleId}")
	@Transactional
	public Response removeRole(@PathParam("userId") UUID userId, @PathParam("roleId") UUID roleId) {
		User user = userRepository.findById(userId);
		Role r = roleRepository.findById(roleId);
		if (user == null || r == null) return Response.status(Response.Status.NOT_FOUND).build();

		user.removeRole(r);
		return Response.ok(UserMapper.toDTO(user)).build();
	}
}