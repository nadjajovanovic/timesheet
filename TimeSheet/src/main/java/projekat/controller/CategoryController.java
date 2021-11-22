package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.CategoryApi;
import projekat.api.model.CategoryDTO;
import projekat.mapper.CategoryMapper;
import projekat.services.CategoryService;

import java.util.List;

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
		return new ResponseEntity<>(CategoryMapper.toCategoryDTO(updated), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Integer categoryid) {
		categoryService.delete(categoryid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
