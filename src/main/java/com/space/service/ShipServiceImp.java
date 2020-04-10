package com.space.service;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.ErrorMessages;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImp implements ShipService {

    private ShipRepository shipRepository;

    private ShipSpecificationsFilters shipSpecificationsFilters;

    @Autowired
    public void setShipSpecificationsFilters(ShipSpecificationsFilters shipSpecificationsFilters) {
        this.shipSpecificationsFilters = shipSpecificationsFilters;
    }

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getAllShipsWithFilters(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Page<Ship> getAllShipsWithFiltersPageable(Specification<Ship> specification, Pageable page) {
        return shipRepository.findAll(specification, page);
    }

    @Override
    public Integer getCount() {
        return shipRepository.findAll().size();
    }


    @Override
    public Ship createShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
            throw new BadRequestException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        checkShipParams(ship);

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        Double rating = calculateRating(ship);
        ship.setRating(rating);

        return shipRepository.saveAndFlush(ship);
    }

    private void checkShipParams(Ship ship) {
        String name = ship.getName();
        if (name != null)
            if (name.isEmpty() || name.length() >= 50) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("ship's name"));
            }

        String planet = ship.getPlanet();
        if (planet != null)
            if (planet.isEmpty() || planet.length() >= 50) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("planet's name"));
            }

        Double speed = ship.getSpeed();
        if (speed != null)
            if (speed.isNaN() || (speed < 0.01D || speed > 0.99D)) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("ship's speed"));
            }

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null)
            if (crewSize < 1 || crewSize > 9999) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("ship's crew number"));
            }

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            int prodYear = calendar.get(Calendar.YEAR);
            if (prodYear < 2800 || prodYear > 3019) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("ship's production date"));
            }
        }
    }

    private Double calculateRating(Ship ship) {
        double speed = ship.getSpeed();
        double k = ship.getUsed() ? 0.5D : 1.0D;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);

        BigDecimal rating = BigDecimal.valueOf((80 * speed * k) / (3019 - year + 1));
        return rating.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public Ship getShip(String id) {
        Long correctID = checkAndParseId(id);
        Optional<Ship> ship = shipRepository.findById(correctID);
        if (!ship.isPresent()) {
            throw new ShipNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        return ship.get();
    }

    private Long checkAndParseId(String id) {
        try {
            if (id == null || id.startsWith("-") || id.equals("0")) {
                throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("Incorrect ID"));
            }
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new BadRequestException(ErrorMessages.INCORRECT_DATA_PARAMS.getErrorMessage("ID isn't a number"));
        }
    }

    @Override
    public Ship updateShip(String id, Ship updates) {
        Ship ship = getShip(id);

        checkShipParams(updates);

        String name = updates.getName();
        if (name != null) {
            ship.setName(name);
        }
        String planet = updates.getPlanet();
        if (planet != null) {
            ship.setPlanet(planet);
        }
        ShipType shipType = updates.getShipType();
        if (shipType != null) {
            ship.setShipType(shipType);
        }
        Date prodDate = updates.getProdDate();
        if (prodDate != null) {
            ship.setProdDate(prodDate);
        }
        Boolean isUsed = updates.getUsed();
        if (isUsed != null) {
            ship.setUsed(isUsed);
        }

        Double speed = updates.getSpeed();
        if (speed != null) {
            ship.setSpeed(speed);
        }

        Integer crewSize = updates.getCrewSize();
        if (crewSize != null) {
            ship.setCrewSize(crewSize);
        }

        Double rating = calculateRating(ship);
        ship.setRating(rating);

        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public void deleteShip(String id) {
        Ship ship = getShip(id);
        shipRepository.delete(ship);
    }

    @Override
    public Specification<Ship> filterByName(String name) {
        return shipSpecificationsFilters.filterByName(name);
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return shipSpecificationsFilters.filterByPlanet(planet);
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return shipSpecificationsFilters.filterByShipType(shipType);
    }

    @Override
    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return shipSpecificationsFilters.filterByUsage(isUsed);
    }

    @Override
    public Specification<Ship> filterBeforeDate(Long before) {
        return shipSpecificationsFilters.filterBeforeDate(before);
    }

    @Override
    public Specification<Ship> filterAfterDate(Long after) {
        return shipSpecificationsFilters.filterAfterDate(after);
    }

    @Override
    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return shipSpecificationsFilters.filterBySpeed(min, max);
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return shipSpecificationsFilters.filterByCrewSize(min, max);
    }

    @Override
    public Specification<Ship> filterByRating(Double min, Double max) {
        return shipSpecificationsFilters.filterByRating(min, max);
    }
}
