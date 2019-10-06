package pl.kursspringboot.cars.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.jaxrs.JaxRsLinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kursspringboot.cars.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/cars")
public class CarController {

    private List<Car> carList;

    @Autowired
    public CarController() {
        this.carList = new ArrayList<>();
        carList.add(new Car(1L, "BMW", "Z3", "Blue"));
        carList.add(new Car(2L, "Audi", "80", "Black"));
        carList.add(new Car(3L, "Fiat", "126p", "Red"));
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Car>> getCars() {
        return new ResponseEntity<>(carList, HttpStatus.OK);
    }


    //Z Hateoas, ale bez HttpStausow
    @GetMapping("/{id}")
    public Resource<Car> getCarById(@PathVariable long id) {
        return carList.stream().filter(car -> id == car.getId()).findFirst()
                .map(car -> {
                    Resource<Car> carResource = new Resource<>(car);
                    carResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(CarController.class)
                            .getCarById(id)).withSelfRel());
                    return carResource;
                }).orElse(null);
    }


// Bez Hateoas
//    @GetMapping("/{id}")
//    public ResponseEntity<Car> getCarById(@PathVariable long id) {
//        Optional<Car> first = carList.stream().filter(car -> car.getId() == id).findFirst();
//        if (first.isPresent()) {
//            return new ResponseEntity<>(first.get(), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }//

    @GetMapping(params = "color")
    public ResponseEntity<Stream<Car>> getCarByColor(@RequestParam String color) {
        return new ResponseEntity<Stream<Car>>(carList.stream().filter(car -> car.getColor().equals(color)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addCar(@RequestBody Car car) {
        Boolean add = carList.add(car);
        if (add) {
            return new ResponseEntity(HttpStatus.CREATED);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeCar(@PathVariable long id) {
        Optional<Car> first = carList.stream().filter(car -> car.getId() == id).findFirst();
        if (first.isPresent()) {
            return new ResponseEntity<>(carList.remove(first.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PutMapping
    public ResponseEntity replaceCar(@RequestBody Car newcar) {
        Optional<Car> first = carList.stream().filter(car -> car.getId() == newcar.getId()).findFirst();
        if (first.isPresent()) {
            carList.remove(first.get());
            carList.add(newcar);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PatchMapping("/{id}")
    public ResponseEntity updateCarElements(@PathVariable long id, @RequestBody Car newCar) {
        Optional<Car> first = carList.stream().filter(car -> car.getId() == id).findFirst();
        if (first.isPresent()) {
            if (newCar.getColor() != null) {
                first.get().setColor(newCar.getColor());
            }
            if (newCar.getMark() != null) {
                first.get().setMark(newCar.getMark());
            }
            if (newCar.getModel() != null) {
                first.get().setModel(newCar.getModel());
            }


            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


}
