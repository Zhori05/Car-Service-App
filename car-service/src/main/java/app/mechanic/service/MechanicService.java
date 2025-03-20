package app.mechanic.service;

import app.mechanic.model.Mechanic;
import app.mechanic.repository.MechanicRepository;
import app.web.dto.AddMechanicRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MechanicService {
    private final MechanicRepository mechanicRepository;

    @Autowired
    public MechanicService(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    public Mechanic addMechanic(AddMechanicRequest addMechanicRequest) {
        Mechanic mechanic = mechanicRepository.save(initializeMechanic(addMechanicRequest));
        return mechanic;
    }

    private Mechanic initializeMechanic(AddMechanicRequest addMechanicRequest){
        return Mechanic.builder()
                .name(addMechanicRequest.getName())
                .email(addMechanicRequest.getEmail())
                .logInCode(addMechanicRequest.getLogInCode())
                .specialisedIn(addMechanicRequest.getSpecialisedIn())
                .build();
    }

    public List<Mechanic> getAllMechanics() {
        return mechanicRepository.findAll();
    }

    public Mechanic getById(UUID userId) {
        return  mechanicRepository.getById(userId);
    }
}