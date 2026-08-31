package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
