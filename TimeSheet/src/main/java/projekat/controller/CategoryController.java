package projekat.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Category;
import projekat.repository.CategoryRepository;

@RestController
public class CategoryController {
	
	private CategoryRepository categoryRepository;
	
	@GetMapping("category")
	public Collection<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
	
	@GetMapping("category/{categoryId}")
	public Category getCategory(@PathVariable Integer categoryId) {
		return categoryRepository.getById(categoryId);
	}
	
	/*@GetMapping("category/{categoryName}")
	public Collection<Category> findByCategoryName(@PathVariable String categoryName) {
		return categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName);
	}*/
	
	@PostMapping("category")
	public ResponseEntity<Category> insertCategory(@RequestBody Category category) {
		categoryRepository.save(category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("category")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		if(categoryRepository.existsById(category.getCategoryid()))
			categoryRepository.save(category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public ResponseEntity<Category> deleteCategory(@PathVariable Integer categoryId) {
		if(categoryRepository.existsById(categoryId))
			categoryRepository.deleteById(categoryId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
