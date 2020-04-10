package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    List<Ship> getAllShipsWithFilters(Specification<Ship> specification);

    Page<Ship> getAllShipsWithFiltersPageable(Specification<Ship> specification, Pageable page);

    Integer getCount();

    Ship createShip(Ship ship);

    Ship getShip(String id);

    Ship updateShip(String id, Ship updates);

    void deleteShip(String id);

    Specification<Ship> filterByName(String name);

    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByShipType(ShipType shipType);

    Specification<Ship> filterByUsage(Boolean isUsed);

    Specification<Ship> filterBeforeDate(Long before);

    Specification<Ship> filterAfterDate(Long after);

    Specification<Ship> filterBySpeed(Double min, Double max);

    Specification<Ship> filterByCrewSize(Integer min, Integer max);

    Specification<Ship> filterByRating(Double min, Double max);

}
