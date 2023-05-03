package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.payload.request.CategoryRequest;
import it.cgmconsulting.myblog.service.CategoryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("category")//http:localhost:8080/user/...
@SecurityRequirement(name = "myBlogSecurityScheme")
@Validated
public class CategoryController{

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?>save(@RequestBody @Valid CategoryRequest request){
		categoryService.save(new Category(request.getCategoryName()));
       return new ResponseEntity("New category added", HttpStatus.CREATED);

	}

	@PatchMapping//cambiare visibilità della categoria
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ResponseEntity<?>changeVisibility(@RequestBody @Valid CategoryRequest request){

		Optional<Category>cat =categoryService.findById(request.getCategoryName());
		if(!cat.isPresent())
			return new ResponseEntity("Category not found",HttpStatus.NOT_FOUND);

		cat.get().setVisible(!cat.get().isVisible());
		return new ResponseEntity("Category visibility modified",HttpStatus.OK);
	}

	@GetMapping
	@PreAuthorize("hasRole('ROLE_WRITER')")
	public ResponseEntity<?>getCategories(){

			return new ResponseEntity<>(categoryService.findAllByVisibleTrue(),HttpStatus.OK);
	}
	@GetMapping("/all")
	@PreAuthorize("hasRole('ROLE_WRITER')")
	public ResponseEntity<?>getAllCategories() {
		return new ResponseEntity<>(categoryService.findAllByOrderByCategoryName(), HttpStatus.OK);

	}
	}
