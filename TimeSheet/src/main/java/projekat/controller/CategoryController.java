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

import projekat.models.Category;;
import projekat.services.CategoryService;

@RestController
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("category")
	public ResponseEntity<Collection<Category>> getAllCategories() {
		final var categories = categoryService.getAll();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
	
	@GetMapping("category/{categoryid}")
	public ResponseEntity<Category> getCategory(@PathVariable Integer categoryid) {
		final var optionalCategory = categoryService.getOne(categoryid);
		if (optionalCategory.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(optionalCategory.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("category")
	public ResponseEntity<Category> insertCategory(@RequestBody Category category) {
		if ( category.getCategoryname() == null || category.getCategoryname().trim().equals("")
				|| category.getCategoryid() != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var insertedCategory = categoryService.create(category);
		return new ResponseEntity<>(insertedCategory, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("category")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		if ( category.getCategoryname() == null || category.getCategoryname().trim().equals("")
				|| category.getCategoryid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updatedCategory = categoryService.update(category);
		if (updatedCategory == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("category/{categoryid}")
	public ResponseEntity<Category> deleteCategory(@PathVariable Integer categoryid) {
		final var deleted = categoryService.delete(categoryid);
		if(!deleted){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
