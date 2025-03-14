package app.serviceForCars.repository;

import app.serviceForCars.model.ServiceForCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceForCarRepository extends JpaRepository<ServiceForCar, UUID > {
}
