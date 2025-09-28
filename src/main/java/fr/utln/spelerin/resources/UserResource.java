package fr.utln.spelerin.resources;

import fr.utln.spelerin.entities.User;
import jakarta.transaction.Transactional;
import fr.utln.spelerin.repositories.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;


import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.entities.Guild;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	private final UserRepository userRepository;
	private final GuildRepository guildRepository;

	@Inject
	public UserResource(UserRepository userRepository, GuildRepository guildRepository) {
		this.userRepository = userRepository;
		this.guildRepository = guildRepository;
	}

	@GET
	public List<User> getAllUsers() {
		return userRepository.listAll();
	}

	@GET
	@Path("/{id}")
	public Response getUserById(@PathParam("id") UUID id) {
		return userRepository.findByIdOptional(id)
				.map(Response::ok)
				.orElse(Response.status(Response.Status.NOT_FOUND))
				.build();
	}

	@POST
	@Transactional
	public Response createUser(User user) {
		if (user.getGuilds() != null && !user.getGuilds().isEmpty()) {
			// If guilds are specified, validate that they exist
			for (Guild guild : user.getGuilds()) {
				if (guild.getId() == null) {
					return Response.status(Response.Status.BAD_REQUEST)
						.entity("Guild ID is required").build();
				}
				guild = guildRepository.findById(guild.getId());
				if (guild == null) {
					return Response.status(Response.Status.NOT_FOUND)
						.entity("Guild not found").build();
				}
			}
		}

		userRepository.persist(user);
		return Response.status(Response.Status.CREATED).entity(user).build();
	}

	@PUT
	@Path("/{id}")
	@Transactional
	public Response updateUser(@PathParam("id") UUID id, User updatedUser) {
		return userRepository.findByIdOptional(id)
				.map(user -> {
					user.setUsername(updatedUser.getUsername());
					user.setDisplayName(updatedUser.getDisplayName());
					userRepository.persist(user);
					return Response.ok(user).build();
				})
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response deleteUser(@PathParam("id") UUID id) {
		boolean deleted = userRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}