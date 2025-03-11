package app.car.service;

import app.car.model.Car;
import app.car.repository.CarRepository;
import app.user.model.User;
import app.web.dto.AddCarRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car addCar(AddCarRequest addCarRequest, User user){
        Car car = carRepository.save(initializeCar(addCarRequest,user));
        return car;
    }

    private Car initializeCar(AddCarRequest addCarRequest, User user) {
        return Car.builder()
                .owner(user)
                .brand(addCarRequest.getBrand())
                .model(addCarRequest.getModel())
                .year(addCarRequest.getYear())
                .color(addCarRequest.getColor())
                .licensePlate(addCarRequest.getLicensePlate())
                .mileage(addCarRequest.getMileage())
                .picture(addCarRequest.getPicture())
                .build();
    }
    public List<Car> getCarsByOwnerId(UUID ownerId) {
        return carRepository.findByOwnerId(ownerId);
    }


}
