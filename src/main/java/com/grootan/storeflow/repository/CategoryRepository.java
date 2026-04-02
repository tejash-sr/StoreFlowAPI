package com.grootan.storeflow.repository;

import com.grootan.storeflow.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByParentId(UUID parentId);
    List<Category> findByParentIsNull();
}
