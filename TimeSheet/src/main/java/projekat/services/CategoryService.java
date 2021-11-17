package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
import projekat.exception.InputFieldException;
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
        final var category = categoryRepository.findById(id);
        return category;
    }

    public Category create(Category category) {
        if (category.getCategoryid() != null) {
            throw new InputFieldException("Id is present in request", ErrorCode.ID_EXISTS);
        }
        final var insertedCategory = categoryRepository.save(category);
        return insertedCategory;
    }

    public Category update(Category category) {
        if (category.getCategoryid() == null) {
            throw new InputFieldException("Id is not present in request", ErrorCode.ID_NOT_FOUND);
        }
        if(!categoryRepository.existsById(category.getCategoryid())){
            return null;
        }
        final var updatedCategory = categoryRepository.save(category);
        return updatedCategory;
    }

    public boolean delete(Integer id){
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }
}
