package fr.utln.spelerin.repositories;

import java.util.UUID;

import fr.utln.spelerin.entities.Guild;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class GuildRepository implements PanacheRepositoryBase<Guild, UUID>{
}