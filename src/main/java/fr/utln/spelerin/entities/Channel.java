package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel{
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String type;

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
}