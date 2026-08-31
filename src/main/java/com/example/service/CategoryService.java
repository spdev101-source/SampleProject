package com.example.service;

import java.util.Optional;

import com.example.request.dto.CategoryRequestDTO;
import com.example.response.dto.CategoryResponseDTO;

public interface CategoryService {

	CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);
	Optional<CategoryResponseDTO> getCategoryById(Long id);
	CategoryResponseDTO updateCategory(Long id,CategoryRequestDTO requestDTO);
}
