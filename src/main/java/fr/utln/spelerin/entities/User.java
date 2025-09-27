package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User{

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String displayName;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;

	@Builder.Default
	@ManyToMany(mappedBy = "users")
	private Set<Guild> guilds = new HashSet<>();

	@Builder.Default
	@ManyToMany
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
}