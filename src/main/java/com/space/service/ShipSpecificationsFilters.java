package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Calendar;

@Component
public class ShipSpecificationsFilters {

    public Specification<Ship> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, cb) -> planet == null ? null : cb.like(root.get("planet"), "%" + planet + "%");
    }

    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, cb) -> shipType == null ? null : cb.equal(root.get("shipType"), shipType);
    }

    public Specification<Ship> filterBeforeDate(Long before) {
        return (root, query, cb) -> {
            if (before == null) {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(before);
            return cb.lessThan(root.get("prodDate"), cal.getTime());
        };
    }

    public Specification<Ship> filterAfterDate(Long after) {
        return (root, query, cb) -> {
            if (after == null) {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(after);

            return cb.greaterThanOrEqualTo(root.get("prodDate"), cal.getTime());
        };
    }

    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return (root, query, cb) -> {
            if (isUsed == null) {
                return null;
            }
            if (isUsed) {
                return cb.isTrue(root.get("isUsed"));
            } else
                return cb.isFalse(root.get("isUsed"));
        };
    }

    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return cb.lessThanOrEqualTo(root.get("speed"), max);
            }
            if (max == null) {
                return cb.greaterThanOrEqualTo(root.get("speed"), min);
            } else
                return cb.between(root.get("speed"), min, max);
        };
    }

    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return cb.lessThanOrEqualTo(root.get("crewSize"), max);
            }
            if (max == null) {
                return cb.greaterThanOrEqualTo(root.get("crewSize"), min);
            }
            return cb.between(root.get("crewSize"), min, max);
        };
    }

    public Specification<Ship> filterByRating(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return cb.lessThanOrEqualTo(root.get("rating"), max);
            }
            if (max == null) {
                return cb.greaterThanOrEqualTo(root.get("rating"), min);
            } else
                return cb.between(root.get("rating"), min, max);
        };
    }
}
