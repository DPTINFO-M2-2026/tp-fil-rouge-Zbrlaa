package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, String>{
}