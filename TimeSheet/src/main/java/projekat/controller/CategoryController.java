package projekat.controller;

import java.util.Collection;
import java.util.List;

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

import projekat.api.api.CategoryApi;
import projekat.api.model.CategoryDTO;
import projekat.mapper.CategoryMapper;
import projekat.models.Category;;
import projekat.services.CategoryService;

@RestController
public class CategoryController implements CategoryApi {
	
	@Autowired
	private CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@Override
	public ResponseEntity<List<CategoryDTO>> getAllCategories() {
		final var categories = categoryService.getAll()
				.stream()
				.map(CategoryMapper::toCategoryDTO)
				.toList();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable Integer categoryid) {
		final var oneCategory = categoryService.getOne(categoryid);
		if (oneCategory.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(CategoryMapper.toCategoryDTO(oneCategory.get()), HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CategoryDTO> insertCategory(@RequestBody CategoryDTO category) {
		final var inserted = categoryService.create(CategoryMapper.toCategory(category));
		return new ResponseEntity<>(CategoryMapper.toCategoryDTO(inserted), HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO category) {
		final var updated = categoryService.update(CategoryMapper.toCategory(category));
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(CategoryMapper.toCategoryDTO(updated), HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Integer categoryid) {
		final var deleted = categoryService.delete(categoryid);
		if(!deleted){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
