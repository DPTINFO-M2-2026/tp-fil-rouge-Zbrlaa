package fr.utln.spelerin.services;

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

@ApplicationScoped
@Transactional
public class InvitationService {

	private final InvitationMapper invitationMapper;
	private final InvitationRepository invitationRepository;
	private final RoleRepository roleRepository;
	private final GuildRepository guildRepository;

	@Inject
	public InvitationService(InvitationMapper invitationMapper, InvitationRepository invitationRepository, 
							 RoleRepository roleRepository, GuildRepository guildRepository) {
		this.invitationMapper = invitationMapper;
		this.invitationRepository = invitationRepository;
		this.roleRepository = roleRepository;
		this.guildRepository = guildRepository;
	}

	public List<InvitationDTO> getAllInvitations() {
		return invitationRepository.listAll().stream().map(invitationMapper::toDTO).toList();
	}

	public Optional<InvitationDTO> getInvitationById(Long id) {
		return invitationRepository.findByIdOptional(id).map(invitationMapper::toDTO);
	}

	public InvitationDTO createInvitation(InvitationCreateDTO dto) {
		Role role = roleRepository.findById(dto.roleId());
		if (role == null) throw new NoSuchElementException("Role not found: " + dto.roleId());

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) throw new NoSuchElementException("Guild not found: " + dto.guildId());

		Invitation invitation = invitationMapper.toEntity(dto, role, guild);
		invitationRepository.persist(invitation);
		return invitationMapper.toDTO(invitation);
	}

	public InvitationDTO updateInvitation(Long id, InvitationUpdateDTO dto) {
		Invitation invitation = invitationRepository.findById(id);
		if (invitation == null) throw new NoSuchElementException("Invitation not found: " + id);

		Role role = roleRepository.findById(dto.roleId()) != null ? roleRepository.findById(dto.roleId()) : invitation.getRole();
		if (role == null) throw new NoSuchElementException("Role not found");

		Guild guild = guildRepository.findById(dto.guildId()) != null ? guildRepository.findById(dto.guildId()) : invitation.getGuild();
		if (guild == null) throw new NoSuchElementException("Guild not found");

		invitationMapper.updateEntity(invitation, dto, role, guild);
		return invitationMapper.toDTO(invitation);
	}

	public boolean deleteInvitation(Long id) {
		return invitationRepository.deleteById(id);
	}
}