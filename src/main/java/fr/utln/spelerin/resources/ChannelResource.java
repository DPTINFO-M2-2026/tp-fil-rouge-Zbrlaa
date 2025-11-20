package fr.utln.spelerin.resources;

import fr.utln.spelerin.dto.createupdatedto.ChannelCreateUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.mappers.ChannelMapper;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;


@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {
	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;

	@Inject
	public ChannelResource(ChannelRepository channelRepository, GuildRepository guildRepository, RoleRepository roleRepository) {
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
	}

	// --- CRUD de base ---

	@GET
	public Response getAll() {
		return Response.ok(
				channelRepository.listAll()
						.stream()
						.map(ChannelMapper::toDTO)
						.toList()
		).build();
	}

	@GET
	@Path("{id}")
	public Response getById(@PathParam("id") UUID id) {
		Channel channel = channelRepository.findById(id);
		if (channel == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(ChannelMapper.toDTO(channel)).build();
	}

	@POST
	@Transactional
	public Response create(ChannelCreateUpdateDTO dto) {
		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Guild not found").build();
		}

		Channel channel = ChannelMapper.toEntity(dto, guild);
		channelRepository.persist(channel);
		return Response.status(Response.Status.CREATED)
				.entity(ChannelMapper.toDTO(channel))
				.build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Response update(@PathParam("id") UUID id, ChannelCreateUpdateDTO dto) {
		Channel channel = channelRepository.findById(id);
		if (channel == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Guild not found").build();
		}

		ChannelMapper.updateEntity(channel, dto, guild);
		channelRepository.persist(channel);
		return Response.ok(ChannelMapper.toDTO(channel)).build();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response delete(@PathParam("id") UUID id) {
		Channel channel = channelRepository.findById(id);
		if (channel == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		channelRepository.delete(channel);
		return Response.noContent().build();
	}

	// --- Relations Role ---

	@PUT
	@Path("{channelId}/roles/{roleId}")
	@Transactional
	public Response addRole(@PathParam("channelId") UUID channelId, @PathParam("roleId") UUID roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);
		if (channel == null || role == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		channel.addRoleWithAccess(role);
		return Response.ok(ChannelMapper.toDTO(channel)).build();
	}

	@DELETE
	@Path("{channelId}/roles/{roleId}")
	@Transactional
	public Response removeRole(@PathParam("channelId") UUID channelId, @PathParam("roleId") UUID roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);
		if (channel == null || role == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		channel.removeRoleWithAccess(role);
		return Response.ok(ChannelMapper.toDTO(channel)).build();
	}
}