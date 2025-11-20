package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.RoleMapper;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import fr.utln.spelerin.repositories.UserRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;


@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {
	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	@Inject
	public RoleResource(ChannelRepository channelRepository, GuildRepository guildRepository, RoleRepository roleRepository, UserRepository userRepository) {
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@GET
	public Response getAll() {
		return Response.ok(
				roleRepository.listAll()
						.stream()
						.map(RoleMapper::toDTO)
						.toList()
		).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") UUID id) {
		Role role = roleRepository.findById(id);
		if (role == null) return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(RoleMapper.toDTO(role)).build();
	}

	@POST
	@Transactional
	public Response create(RoleCreateUpdateDTO dto) {
		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) return Response.status(Response.Status.NOT_FOUND).build();

		Role role = RoleMapper.toEntity(dto, guild);
		roleRepository.persist(role);

		return Response.status(Response.Status.CREATED).entity(RoleMapper.toDTO(role)).build();
	}

	@PUT
	@Path("/{id}")
	@Transactional
	public Response update(@PathParam("id") UUID id, RoleCreateUpdateDTO dto) {
		Role role = roleRepository.findById(id);
		if (role == null) return Response.status(Response.Status.NOT_FOUND).build();

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) return Response.status(Response.Status.BAD_REQUEST).build();

		RoleMapper.updateEntity(role, dto, guild);
		return Response.ok(RoleMapper.toDTO(role)).build();
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response delete(@PathParam("id") UUID id) {
		boolean deleted = roleRepository.deleteById(id);
		return deleted ? Response.noContent().build()
					: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations User ---

	@PUT
	@Path("/{roleId}/users/{userId}")
	@Transactional
	public Response addUser(@PathParam("roleId") UUID roleId, @PathParam("userId") UUID userId) {
		Role role = roleRepository.findById(roleId);
		User user = userRepository.findById(userId);
		if (role == null || user == null) return Response.status(Response.Status.NOT_FOUND).build();

		role.addUser(user);
		return Response.ok(RoleMapper.toDTO(role)).build();
	}

	@DELETE
	@Path("/{roleId}/users/{userId}")
	@Transactional
	public Response removeUser(@PathParam("roleId") UUID roleId, @PathParam("userId") UUID userId) {
		Role role = roleRepository.findById(roleId);
		User user = userRepository.findById(userId);
		if (role == null || user == null) return Response.status(Response.Status.NOT_FOUND).build();

		role.removeUser(user);
		return Response.ok(RoleMapper.toDTO(role)).build();
	}

	// --- Relations Channel ---

	@PUT
	@Path("/{roleId}/channels/{channelId}")
	@Transactional
	public Response addChannel(@PathParam("roleId") UUID roleId, @PathParam("channelId") UUID channelId) {
		Role role = roleRepository.findById(roleId);
		Channel channel = channelRepository.findById(channelId);
		if (role == null || channel == null) return Response.status(Response.Status.NOT_FOUND).build();

		role.addAccessibleChannel(channel);
		return Response.ok(RoleMapper.toDTO(role)).build();
	}

	@DELETE
	@Path("/{roleId}/channels/{channelId}")
	@Transactional
	public Response removeChannel(@PathParam("roleId") UUID roleId, @PathParam("channelId") UUID channelId) {
		Role role = roleRepository.findById(roleId);
		Channel channel = channelRepository.findById(channelId);
		if (role == null || channel == null) return Response.status(Response.Status.NOT_FOUND).build();

		role.removeAccessibleChannel(channel);
		return Response.ok(RoleMapper.toDTO(role)).build();
	}
}