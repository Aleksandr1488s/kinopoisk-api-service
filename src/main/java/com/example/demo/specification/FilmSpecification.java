package com.example.demo.specification;

import com.example.demo.entity.Film;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class FilmSpecification {
    public static Specification<Film> withFilters(String filmName, Integer year, Double ratingFrom, Double ratingTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filmName != null && !filmName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("filmName")), "%" + filmName.toLowerCase() + "%"));
            }
            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }
            if (ratingFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), ratingFrom));
            }
            if (ratingTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), ratingTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        }
    }

