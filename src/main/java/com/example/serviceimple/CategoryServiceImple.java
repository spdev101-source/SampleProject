package com.example.serviceimple;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import com.example.request.dto.CategoryRequestDTO;
import com.example.response.dto.CategoryResponseDTO;
import com.example.service.CategoryService;

@Service
public class CategoryServiceImple implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	public CategoryServiceImple(CategoryRepository categoryRepository, ModelMapper modelMapper) {
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
		if (categoryRepository.existsByCategoryname(requestDTO.getCategoryname())) {
			throw new RuntimeException("Category Already exists with Name:"+requestDTO.getCategoryname());
		}
		if(categoryRepository.existsByCode(requestDTO.getCode()))
		{
			throw new RuntimeException("Category Already exists with Code:"+requestDTO.getCode());
		}
		Category category = new Category();
		category.setCode(requestDTO.getCode());
		category.setCategoryname(requestDTO.getCategoryname());
		category.setDescription(requestDTO.getDescription());
		category.setCreatedat(LocalDateTime.now());
		Category saved = categoryRepository.save(category);
		return modelMapper.map(saved, CategoryResponseDTO.class);
	}

	@Override
	public Optional<CategoryResponseDTO> getCategoryById(Long id) {
		return categoryRepository.findById(id).map(category -> modelMapper.map(category, CategoryResponseDTO.class));
	}

	@Override
	public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found with id:" + id));
		if(categoryRepository.existsByCategorynameAndIdNot(requestDTO.getCategoryname(), id))
		{
			throw new RuntimeException("Category Already exists with Name:");
		}
		category.setCategoryname(requestDTO.getCategoryname());
		category.setDescription(requestDTO.getDescription());
		category.setUpdatedat(LocalDateTime.now());
		Category updated=categoryRepository.save(category);
		return modelMapper.map(updated,CategoryResponseDTO.class);
	}

}
