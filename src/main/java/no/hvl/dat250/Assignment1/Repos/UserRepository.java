package no.hvl.dat250.Assignment1.Repos;

import no.hvl.dat250.Assignment1.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
