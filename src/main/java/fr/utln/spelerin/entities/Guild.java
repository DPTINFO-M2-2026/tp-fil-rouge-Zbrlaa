package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "guilds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(value = {"users", "roles", "channels"}, ignoreUnknown = true)
public class Guild {
	@Id
	@GeneratedValue
	@UuidGenerator
	@ToString.Include
	private UUID id;

	@Column(nullable = false)
	@ToString.Include
	private String name;

	@Column(name = "created_at", nullable = false)
	@ToString.Include
	private Instant createdAt;

	// @JsonIgnoreProperties({"guilds", "roles"})
	@Builder.Default
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
		name = "guild_users",
		joinColumns = @JoinColumn(name = "guild_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<User> users = new HashSet<>();

	// @JsonIgnoreProperties({"guild", "users", "accessibleChannels"})
	@Builder.Default
	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Role> roles = new HashSet<>();

	// @JsonIgnoreProperties({"guild", "rolesWithAccess"})
	@Builder.Default
	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Channel> channels = new HashSet<>();

	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}


	public void addUser(User user) {
		if (user == null) return;
		if (this.users.add(user)) {
			user.getGuilds().add(this);
		}
	}

	public void removeUser(User user) {
		if (user == null) return;
		if (this.users.remove(user)) {
			user.getGuilds().remove(this);
		}
	}

	public void addRole(Role role) {
		if (role == null) return;
		if (this.roles.add(role)) {
			role.setGuild(this);
		}
	}

	public void removeRole(Role role) {
		if (role == null) return;
		if (this.roles.remove(role)) {
			role.setGuild(null);
		}
	}

	public void addChannel(Channel channel) {
		if (channel == null) return;
		if (this.channels.add(channel)) {
			channel.setGuild(this);
		}
	}

	public void removeChannel(Channel channel) {
		if (channel == null) return;
		if (this.channels.remove(channel)) {
			channel.setGuild(null);
		}
	}


	@ToString.Include(name = "userIds")
	public Set<UUID> getUserIds() {
		return users.stream()
				.map(User::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "roleIds")
	public Set<UUID> getRoleIds() {
		return roles.stream()
				.map(Role::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "channelIds")
	public Set<UUID> getChannelIds() {
		return channels.stream()
				.map(Channel::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}
}