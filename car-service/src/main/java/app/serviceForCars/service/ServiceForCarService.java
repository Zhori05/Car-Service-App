package app.serviceForCars.service;

import app.car.model.Car;
import app.serviceForCars.model.ServiceForCar;
import app.serviceForCars.repository.ServiceForCarRepository;
import app.user.model.User;
import app.web.dto.AddCarRequest;
import app.web.dto.AddServiceRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceForCarService {
    private final ServiceForCarRepository serviceForCarRepository;

    @Autowired
    public ServiceForCarService(ServiceForCarRepository serviceForCarRepository) {
        this.serviceForCarRepository = serviceForCarRepository;
    }

    public ServiceForCar addService( AddServiceRequest addServiceRequest) {
        ServiceForCar serviceForCar = serviceForCarRepository.save(initializeServiceForCar(addServiceRequest));
        return serviceForCar;

    }
    private ServiceForCar initializeServiceForCar(AddServiceRequest addServiceRequest) {
        return ServiceForCar.builder()
                .name(addServiceRequest.getName())
                .minutesToFinish(addServiceRequest.getMinutesToFinish())
                .price(addServiceRequest.getPrice())
                .special(addServiceRequest.getSpecial())
                .build();
    }
}
