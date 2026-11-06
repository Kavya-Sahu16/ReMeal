package com.kavya.remeal.donation;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DonationSpecification {

    public static Specification<Donation> hasStatus(
            DonationStatus status) {

        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Donation> isNotExpired(
            LocalDateTime now) {

        return (root, query, cb) ->
                cb.greaterThan(root.get("expiryDateTime"), now);
    }

    public static Specification<Donation> hasCity(String city) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("city")),
                        "%" + city.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Donation> hasCategory(
            DonationCategory category) {

        return (root, query, cb) ->
                cb.equal(root.get("category"), category);
    }

    public static Specification<Donation> hasMinimumQuantity(
            BigDecimal minQuantity) {

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("quantity"), minQuantity
                );
    }

    public static Specification<Donation> hasMaximumQuantity(
            BigDecimal maxQuantity) {

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("quantity"), maxQuantity
                );
    }

    public static Specification<Donation> expiresAfter(
            LocalDateTime expiryFrom) {

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("expiryDateTime"), expiryFrom
                );
    }

    public static Specification<Donation> expiresBefore(
            LocalDateTime expiryTo) {

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("expiryDateTime"), expiryTo
                );
    }

    public static Specification<Donation> hasFoodName(String foodName) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("foodName")),
                        "%" + foodName.trim().toLowerCase() + "%"
                );
    }
}