package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "id"
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

	@ManyToOne
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

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


	// toString helpers
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