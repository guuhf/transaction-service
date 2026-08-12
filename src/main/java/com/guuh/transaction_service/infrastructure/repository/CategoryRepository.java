package com.guuh.transaction_service.infrastructure.repository;

import com.guuh.transaction_service.infrastructure.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndUserId(String name, Long userId);

    List<Category> findByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
