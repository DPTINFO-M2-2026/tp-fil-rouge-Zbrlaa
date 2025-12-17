package fr.utln.spelerin.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Invitation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ToString.Include
	private Long id;

	@Column(nullable = false, unique = true)
	@ToString.Include
	private String discordCode;

	@Builder.Default
	@ManyToMany
	@JoinTable(
		name = "invitation_roles",
		joinColumns = @JoinColumn(name = "invitation_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	
	public void addRole(Role role) {
		if (role == null) return;
		if(this.roles.add(role)) {
			role.getInvitations().add(this);
		}
	}

	public void removeRole(Role role) {
		if (role == null) return;
		if(this.roles.remove(role)) {
			role.getInvitations().remove(this);
		}
	}


	@ToString.Include(name = "guildId")
	public long getGuildId() {
		return guild != null ? guild.getId() : 0L;
	}

	@ToString.Include(name = "roleIds")
	public Set<Long> getRoleIds() {
		return roles.stream()
			.map(Role::getId)
			.collect(Collectors.toUnmodifiableSet());
	}
}