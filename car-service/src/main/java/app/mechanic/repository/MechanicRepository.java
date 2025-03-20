package app.mechanic.repository;

import app.mechanic.model.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MechanicRepository extends JpaRepository<Mechanic, UUID> {
    Optional<Mechanic> findByEmail(String email);
}
