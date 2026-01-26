package fr.utln.spelerin.repositories;

import java.util.Optional;

import fr.utln.spelerin.entities.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long>{
	public Optional<User> findByUsernameOptional(String username) {
		return find("username", username).firstResultOptional();
	}
}