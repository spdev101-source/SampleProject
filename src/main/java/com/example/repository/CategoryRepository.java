package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
boolean existsByCategoryname(String categoryname);
boolean existsByCategorynameAndIdNot(String name,Long id);
}
