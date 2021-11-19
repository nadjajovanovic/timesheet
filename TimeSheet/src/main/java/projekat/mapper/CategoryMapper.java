package projekat.mapper;

import projekat.api.model.CategoryDTO;
import projekat.models.Category;

public class CategoryMapper {

    public static CategoryDTO toCategoryDTO(Category category) {
        final var categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getCategoryid());
        categoryDTO.setName(category.getCategoryname());
        return  categoryDTO;
    }

    public static Category toCategory (CategoryDTO categoryDTO) {
        final var category = new Category();
        category.setCategoryid(categoryDTO.getId());
        category.setCategoryname(categoryDTO.getName());
        return category;
    }
    
}
