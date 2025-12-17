package fr.utln.spelerin.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "guilds")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Guild {
	@Id
	@ToString.Include
	private long id;

	@Column(nullable = false)
	@ToString.Include
	private String name;

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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

	@Builder.Default
	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Invitation> invitations = new HashSet<>();


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

	public void addInvitation(Invitation invitation) {
		if (invitation == null) return;
		if (this.invitations.add(invitation)) {
			invitation.setGuild(this);
		}
	}

	public void removeInvitation(Invitation invitation) {
		if (invitation == null) return;
		if (this.invitations.remove(invitation)) {
			invitation.setGuild(null);
		}
	}


	@ToString.Include(name = "userIds")
	public Set<Long> getUserIds() {
		return users.stream()
				.map(User::getId)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "roleIds")
	public Set<Long> getRoleIds() {
		return roles.stream()
				.map(Role::getId)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "channelIds")
	public Set<Long> getChannelIds() {
		return channels.stream()
				.map(Channel::getId)
				.collect(Collectors.toUnmodifiableSet());
	}

	@ToString.Include(name = "invitationIds")
	public Set<Long> getInvitationIds() {
		return invitations.stream()
				.map(Invitation::getId)
				.collect(Collectors.toUnmodifiableSet());
	}
}