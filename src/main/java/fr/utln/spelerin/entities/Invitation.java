package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Invitation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ToString.Include
	private Long id;

	@Column(nullable = false, unique = true)
	@ToString.Include
	private String discordCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private Guild guild;

	@ToString.Include(name = "guildId")
	public long getGuildId() {
		return guild != null ? guild.getId() : 0L;
	}

	@ToString.Include(name = "roleId")
	public long getRoleId() {
		return role != null ? role.getId() : 0L;
	}
}