package app.mechanic.repository;

import app.mechanic.model.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MechanicRepository extends JpaRepository<Mechanic, UUID> {
}
