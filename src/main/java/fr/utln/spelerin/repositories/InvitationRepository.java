package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Invitation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class InvitationRepository implements PanacheRepositoryBase<Invitation, Long> {
}