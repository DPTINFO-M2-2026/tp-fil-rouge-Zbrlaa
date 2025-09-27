package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role{
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long permissions;

	@ManyToOne
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	@Builder.Default
	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();

	@Builder.Default
	@ManyToMany(mappedBy = "rolesWithAccess")
	private Set<Channel> accessibleChannels = new HashSet<>();
}