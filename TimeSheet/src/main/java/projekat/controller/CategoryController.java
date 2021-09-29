package projekat.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	@GetMapping("category")
	public Collection<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
	
	@GetMapping("category/{categoryid}")
	public Category getCategory(@PathVariable Integer categoryid) {
		return categoryRepository.getById(categoryid);
	}
	
	/*@GetMapping("category/{categoryName}")
	public Collection<Category> findByCategoryName(@PathVariable String categoryName) {
		return categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName);
	}*/
	
	@CrossOrigin
	@PostMapping("category")
	public ResponseEntity<Category> insertCategory(@RequestBody Category category) {
		categoryRepository.save(category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("category")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		if(categoryRepository.existsById(category.getCategoryid()))
			categoryRepository.save(category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("category/{categoryid}")
	public ResponseEntity<Category> deleteCategory(@PathVariable Integer categoryid) {
		if(categoryRepository.existsById(categoryid))
			categoryRepository.deleteById(categoryid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
