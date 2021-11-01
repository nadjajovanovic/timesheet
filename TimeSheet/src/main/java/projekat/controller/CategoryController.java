package projekat.controller;

import java.util.Collection;
import java.util.Optional;

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
	public ResponseEntity<Category> getCategory(@PathVariable Integer categoryid) {
		Optional<Category> category = categoryRepository.findById(categoryid);
		if (!category.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(category.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("category")
	public ResponseEntity<Category> insertCategory(@RequestBody Category category) {
		if ( category.getCategoryname() == null || category.getCategoryname().trim().equals("")
				|| category.getCategoryid() != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Category cat = categoryRepository.save(category);
		return new ResponseEntity<Category>(cat, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("category")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		if(!categoryRepository.existsById(category.getCategoryid())){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if ( category.getCategoryname() == null || category.getCategoryname().trim().equals("")
				|| category.getCategoryid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Category inserted = categoryRepository.save(category);
		return new ResponseEntity<>(inserted, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("category/{categoryid}")
	public ResponseEntity<Category> deleteCategory(@PathVariable Integer categoryid) {
		if(!categoryRepository.existsById(categoryid)){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		categoryRepository.deleteById(categoryid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
