package projekat.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import projekat.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	Collection<Category> findByCategoryNameContainingIgnoreCase(String categoryName);
}
