package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import projekat.exception.InputFieldException;
import projekat.exception.NotFoundException;
import projekat.models.Category;
import projekat.repository.CategoryRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Collection<Category> getAll() {
        final var categories = categoryRepository.findAll();
        return categories;
    }

    public Optional<Category> getOne(Integer id){
        if (!categoryRepository.existsById(id))
            throw new NotFoundException(String.format("Category with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        final var category = categoryRepository.findById(id);
        return category;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category create(Category category) {
        if (category.getCategoryid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }
        final var insertedCategory = categoryRepository.save(category);
        return insertedCategory;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category update(Category category) {
        if (category.getCategoryid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.NOT_FOUND);
        }
        if(!categoryRepository.existsById(category.getCategoryid())){
            throw new NotFoundException(String.format("Category with id %d does not exist in database", category.getCategoryid()), HttpStatus.NOT_FOUND);
        }
        final var updatedCategory = categoryRepository.save(category);
        return updatedCategory;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(Integer id){
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id %d does not exist in database", id),HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
        return true;
    }
}
