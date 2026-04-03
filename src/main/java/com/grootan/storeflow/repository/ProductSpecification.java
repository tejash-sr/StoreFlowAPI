package com.grootan.storeflow.repository;

import com.grootan.storeflow.models.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasNamePartial(String name) {
        return (root, query, cb) -> 
            name == null || name.isEmpty() ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> 
            category == null || category.isEmpty() ? null : cb.equal(root.get("category").get("name"), category);
    }

    public static Specification<Product> hasStatus(String status) {
        return (root, query, cb) -> 
            status == null || status.isEmpty() ? null : cb.equal(root.get("status").as(String.class), status);
    }

    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }
}
