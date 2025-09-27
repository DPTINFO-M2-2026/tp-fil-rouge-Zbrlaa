package fr.utln.spelerin.repositories;

import java.util.UUID;

import fr.utln.spelerin.entities.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID>{
	public User findByUsername(String username) {
		return find("username", username).firstResult();
	}
}