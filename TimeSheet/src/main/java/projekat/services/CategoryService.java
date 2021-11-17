package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
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
            throw new NotFoundException(String.format("Object with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
        final var category = categoryRepository.findById(id);
        return category;
    }

    public Category create(Category category) {
        final var insertedCategory = categoryRepository.save(category);
        return insertedCategory;
    }

    public Category update(Category category) {
        if(!categoryRepository.existsById(category.getCategoryid())){
            throw new NotFoundException(String.format("Object with id %d does not exist in database", category.getCategoryid()), ErrorCode.NOT_FOUND);
        }
        final var updatedCategory = categoryRepository.save(category);
        return updatedCategory;
    }

    public boolean delete(Integer id){
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Object with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
        return true;
    }
}
