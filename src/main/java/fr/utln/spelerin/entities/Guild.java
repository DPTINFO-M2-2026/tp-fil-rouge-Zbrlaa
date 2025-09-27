package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "guilds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guild{
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Builder.Default
	@ManyToMany
	@JoinTable(
		name = "guild_users",
		joinColumns = @JoinColumn(name = "guild_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<User> users = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Role> roles = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Channel> channels = new HashSet<>();

	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}