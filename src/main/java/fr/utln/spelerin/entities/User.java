package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class User {
	@Id
	@ToString.Include
	private long id;

	@Column(nullable = false)
	@ToString.Include
	private String username;

	@Column(nullable = false)
	@ToString.Include
	private String displayName;

	@Column(name = "joined_at", nullable = false)
	@ToString.Include
	private Instant joinedAt;

	@Builder.Default
	@ManyToMany(mappedBy = "users", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Set<Guild> guilds = new HashSet<>();

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
		name = "user_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();


	@PrePersist
	public void prePersist(){
		if (joinedAt == null) {
			joinedAt = Instant.now();
		}
	}


	public void addGuild(Guild guild) {
		if (guild == null) return;
		if (this.guilds.add(guild)) {
			guild.getUsers().add(this);
		}
	}

	public void removeGuild(Guild guild) {
		if (guild == null) return;
		if (this.guilds.remove(guild)) {
			guild.getUsers().remove(this);
		}
	}

	public void addRole(Role role) {
		if (role == null) return;
		if (this.roles.add(role)) {
			role.getUsers().add(this);
		}
	}

	public void removeRole(Role role) {
		if (role == null) return;
		if (this.roles.remove(role)) {
			role.getUsers().remove(this);
		}
	}


	@ToString.Include(name = "guildIds")
	public Set<Long> getGuildIds() {
		return guilds.stream()
				.map(Guild::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "roleIds")
	public Set<Long> getRoleIds() {
		return roles.stream()
				.map(Role::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}
}