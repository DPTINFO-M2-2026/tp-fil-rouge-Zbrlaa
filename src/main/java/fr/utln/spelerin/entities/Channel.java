package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Channel {
	@Id
	@ToString.Include
	private long id;

	@Column(nullable = false)
	@ToString.Include
	private String name;

	@Column(nullable = false)
	@ToString.Include
	private int type;

	@ManyToOne
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	@Builder.Default
	@ManyToMany
	@JoinTable(
		name = "channel_roles",
		joinColumns = @JoinColumn(name = "channel_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> rolesWithAccess = new HashSet<>();


	public void addRoleWithAccess(Role role) {
		if (role == null) return;
		if (this.rolesWithAccess.add(role)) {
			role.getAccessibleChannels().add(this);
		}
	}

	public void removeRoleWithAccess(Role role) {
		if (role == null) return;
		if (this.rolesWithAccess.remove(role)) {
			role.getAccessibleChannels().remove(this);
		}
	}


	@ToString.Include(name = "guildId")
	public long getGuildId() {
		return guild != null ? guild.getId() : 0L;
	}

	@ToString.Include(name = "roleIds")
	public Set<Long> getRoleIds() {
		return rolesWithAccess.stream()
			.map(Role::getId)
			.collect(Collectors.toUnmodifiableSet());
	}
}