package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(value = {"guild", "users", "accessibleChannels"}, ignoreUnknown = true)
public class Role {
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
	private Long permissions;

	// @JsonIgnoreProperties({"users", "roles", "channels"})
	@ManyToOne
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	// @JsonIgnoreProperties({"guilds", "roles"})
	@Builder.Default
	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();

	// @JsonIgnoreProperties({"guild", "rolesWithAccess"})
	@Builder.Default
	@ManyToMany(mappedBy = "rolesWithAccess")
	private Set<Channel> accessibleChannels = new HashSet<>();


	public void addUser(User user) {
		if (user == null) return;
		if (this.users.add(user)) {
			user.getRoles().add(this);
		}
	}

	public void removeUser(User user) {
		if (user == null) return;
		if (this.users.remove(user)) {
			user.getRoles().remove(this);
		}
	}

	public void addAccessibleChannel(Channel channel) {
		if (channel == null) return;
		if (this.accessibleChannels.add(channel)) {
			channel.getRolesWithAccess().add(this);
		}
	}

	public void removeAccessibleChannel(Channel channel) {
		if (channel == null) return;
		if (this.accessibleChannels.remove(channel)) {
			channel.getRolesWithAccess().remove(this);
		}
	}


	// toString helpers
	@ToString.Include(name = "guildId")
	public UUID getGuildId() {
		return guild != null ? guild.getId() : null;
	}

	@ToString.Include(name = "userIds")
	public Set<UUID> getUserIds() {
		return users.stream()
			.map(User::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "accessibleChannelIds")
	public Set<UUID> getAccessibleChannelIds() {
		return accessibleChannels.stream()
			.map(Channel::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toUnmodifiableSet());
	}
}