package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createupdatedto.GuildCreateUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.GuildMapper;
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


@Path("/guilds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuildResource {
	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	@Inject
	public GuildResource(ChannelRepository channelRepository, GuildRepository guildRepository, RoleRepository roleRepository, UserRepository userRepository) {
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	// --- CRUD de base ---

	@GET
	public Response getAll() {
		return Response.ok(
				guildRepository.listAll()
						.stream()
						.map(GuildMapper::toDTO)
						.toList()
		).build();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") UUID id) {
		Guild guild = guildRepository.findById(id);
		if (guild == null) return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	@POST
	@Transactional
	public Response create(GuildCreateUpdateDTO dto) {
		Guild guild = GuildMapper.toEntity(dto);
		guildRepository.persist(guild);
		return Response.status(Response.Status.CREATED).entity(GuildMapper.toDTO(guild)).build();
	}

	@PUT
	@Path("/{id}")
	@Transactional
	public Response update(@PathParam("id") UUID id, GuildCreateUpdateDTO dto) {
		Guild guild = guildRepository.findById(id);
		if (guild == null) return Response.status(Response.Status.NOT_FOUND).build();

		GuildMapper.updateEntity(guild, dto);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response delete(@PathParam("id") UUID id) {
		boolean deleted = guildRepository.deleteById(id);
		return deleted ? Response.noContent().build()
					: Response.status(Response.Status.NOT_FOUND).build();
	}

	// --- Relations User ---

	@PUT
	@Path("/{guildId}/users/{userId}")
	@Transactional
	public Response addUser(@PathParam("guildId") UUID guildId, @PathParam("userId") UUID userId) {
		Guild guild = guildRepository.findById(guildId);
		User user = userRepository.findById(userId);
		if (guild == null || user == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.addUser(user);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	@DELETE
	@Path("/{guildId}/users/{userId}")
	@Transactional
	public Response removeUser(@PathParam("guildId") UUID guildId, @PathParam("userId") UUID userId) {
		Guild guild = guildRepository.findById(guildId);
		User user = userRepository.findById(userId);
		if (guild == null || user == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.removeUser(user);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	// --- Relations Role ---

	@PUT
	@Path("/{guildId}/roles/{roleId}")
	@Transactional
	public Response addRole(@PathParam("guildId") UUID guildId, @PathParam("roleId") UUID roleId) {
		Guild guild = guildRepository.findById(guildId);
		Role role = roleRepository.findById(roleId);
		if (guild == null || role == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.addRole(role);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	@DELETE
	@Path("/{guildId}/roles/{roleId}")
	@Transactional
	public Response removeRole(@PathParam("guildId") UUID guildId, @PathParam("roleId") UUID roleId) {
		Guild guild = guildRepository.findById(guildId);
		Role role = roleRepository.findById(roleId);
		if (guild == null || role == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.removeRole(role);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	// --- Relations Channel ---

	@PUT
	@Path("/{guildId}/channels/{channelId}")
	@Transactional
	public Response addChannel(@PathParam("guildId") UUID guildId, @PathParam("channelId") UUID channelId) {
		Guild guild = guildRepository.findById(guildId);
		Channel channel = channelRepository.findById(channelId);
		if (guild == null || channel == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.addChannel(channel);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}

	@DELETE
	@Path("/{guildId}/channels/{channelId}")
	@Transactional
	public Response removeChannel(@PathParam("guildId") UUID guildId, @PathParam("channelId") UUID channelId) {
		Guild guild = guildRepository.findById(guildId);
		Channel channel = channelRepository.findById(channelId);
		if (guild == null || channel == null) return Response.status(Response.Status.NOT_FOUND).build();

		guild.removeChannel(channel);
		return Response.ok(GuildMapper.toDTO(guild)).build();
	}
}