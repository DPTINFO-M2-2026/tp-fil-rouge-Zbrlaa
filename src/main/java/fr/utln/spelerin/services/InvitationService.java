package fr.utln.spelerin.services;

import fr.utln.spelerin.clients.BotClient;
import fr.utln.spelerin.dto.InvitationDTO;
import fr.utln.spelerin.dto.createdto.InvitationCreateDTO;
import fr.utln.spelerin.dto.updatedto.InvitationUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Invitation;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.mappers.InvitationMapper;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.InvitationRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Transactional
public class InvitationService {

	private final InvitationMapper invitationMapper;
	private final InvitationRepository invitationRepository;
	private final RoleRepository roleRepository;
	private final GuildRepository guildRepository;
	private final BotClient botClient;

	@Inject
	public InvitationService(InvitationMapper invitationMapper, InvitationRepository invitationRepository, 
				RoleRepository roleRepository, GuildRepository guildRepository, @RestClient BotClient botClient) {
		this.invitationMapper = invitationMapper;
		this.invitationRepository = invitationRepository;
		this.roleRepository = roleRepository;
		this.guildRepository = guildRepository;
		this.botClient = botClient;
	}

	@Transactional
    public InvitationDTO generateAndSaveInvitation(long guildId) {
        String discordCode = botClient.getInviteCode(guildId);

        Guild guild = guildRepository.findById(guildId);
        if (guild == null) {
            throw new NoSuchElementException("Guilde introuvable en base");
        }

        InvitationCreateDTO createDto = new InvitationCreateDTO(discordCode, guildId);
        Invitation entity = invitationMapper.toEntity(createDto, guild);
        invitationRepository.persist(entity);

        return invitationMapper.toDTO(entity);
    }

	public List<InvitationDTO> getAllInvitations() {
		return invitationRepository.listAll().stream().map(invitationMapper::toDTO).toList();
	}

	public Optional<InvitationDTO> getInvitationById(Long id) {
		return invitationRepository.findByIdOptional(id).map(invitationMapper::toDTO);
	}

	public InvitationDTO createInvitation(InvitationCreateDTO dto) {
		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) throw new NoSuchElementException("Guild not found: " + dto.guildId());

		Invitation invitation = invitationMapper.toEntity(dto, guild);
		invitationRepository.persist(invitation);
		return invitationMapper.toDTO(invitation);
	}

	public InvitationDTO updateInvitation(Long id, InvitationUpdateDTO dto) {
		Invitation invitation = invitationRepository.findById(id);
		if (invitation == null) throw new NoSuchElementException("Invitation not found: " + id);

		Guild guild = guildRepository.findById(dto.guildId()) != null ? guildRepository.findById(dto.guildId()) : invitation.getGuild();
		if (guild == null) throw new NoSuchElementException("Guild not found");

		invitationMapper.updateEntity(invitation, dto, guild);
		return invitationMapper.toDTO(invitation);
	}

	public boolean deleteInvitation(Long id) {
		return invitationRepository.deleteById(id);
	}

	public InvitationDTO addRoleToInvitation(Long invitationId, Long roleId) {
		Invitation invitation = invitationRepository.findById(invitationId);
		if (invitation == null) throw new NoSuchElementException("Invitation not found: " + invitationId);

		Role role = roleRepository.findById(roleId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		invitation.getRoles().add(role);
		return invitationMapper.toDTO(invitation);
	}

	public InvitationDTO removeRoleFromInvitation(Long invitationId, Long roleId) {
		Invitation invitation = invitationRepository.findById(invitationId);
		if (invitation == null) throw new NoSuchElementException("Invitation not found: " + invitationId);

		Role role = roleRepository.findById(roleId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		invitation.getRoles().remove(role);
		return invitationMapper.toDTO(invitation);
	}
}