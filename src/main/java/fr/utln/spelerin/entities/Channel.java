package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.UuidGenerator;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
// @JsonIgnoreProperties(value = {"guild", "rolesWithAccess"}, ignoreUnknown = true)
public class Channel {
	@Id
	@GeneratedValue
	@UuidGenerator
	@ToString.Include
	private UUID id;

	@Column(nullable = false)
	@ToString.Include
	private String name;

	@Column(nullable = false)
	@ToString.Include
	private String type;

	// @JsonIgnoreProperties({"users", "roles", "channels"})
	@ManyToOne
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	// @JsonIgnoreProperties({"guild", "users", "accessibleChannels"})
	@Builder.Default
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
	public UUID getGuildId() {
		return guild != null ? guild.getId() : null;
	}

	@ToString.Include(name = "roleIds")
	public Set<UUID> getRoleIds() {
		return rolesWithAccess.stream()
			.map(Role::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toUnmodifiableSet());
	}
}