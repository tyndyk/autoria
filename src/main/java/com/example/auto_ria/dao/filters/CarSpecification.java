package com.example.auto_ria.dao.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.auto_ria.models.car.CarSQL;
import com.example.auto_ria.models.responses.car.CarQuery;

import jakarta.persistence.criteria.Predicate;

public class CarSpecification {
    public static Specification<CarSQL> filterBy(CarQuery carQuery) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (carQuery.getBrand() != null) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand")), carQuery.getBrand().name()));
            }

            if (carQuery.getModel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("model"), carQuery.getModel().name()));
            }
            if (carQuery.getPowerH() != null && carQuery.getPowerH() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("powerH"), carQuery.getPowerH()));
            }
            if (carQuery.isActivated()) {
                predicates.add(criteriaBuilder.equal(root.get("isActivated"), carQuery.isActivated())); // Change from true to false
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
