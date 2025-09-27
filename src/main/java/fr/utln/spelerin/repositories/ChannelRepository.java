package fr.utln.spelerin.repositories;

import java.util.UUID;

import fr.utln.spelerin.entities.Channel;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ChannelRepository implements PanacheRepositoryBase<Channel, UUID>{
}