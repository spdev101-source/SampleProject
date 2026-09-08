package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import com.example.request.dto.CategoryRequestDTO;
import com.example.response.dto.CategoryResponseDTO;
import com.example.service.CategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	private final CategoryService categoryService;
	public CategoryController(CategoryService categoryService)
	{
		this.categoryService=categoryService;
	}
	@PostMapping("/save")
	public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO requestDTO)
	{
		CategoryResponseDTO saved=categoryService.createCategory(requestDTO);
		return new ResponseEntity<CategoryResponseDTO>(saved,HttpStatus.CREATED);
	}
	@GetMapping("/get/{id}")
	public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id)
	{
		CategoryResponseDTO category=categoryService.getCategoryById(id).orElseThrow(()->new RuntimeException("Category not found with id:"+id));
		return new ResponseEntity<CategoryResponseDTO>(category,HttpStatus.OK);
	}
}
