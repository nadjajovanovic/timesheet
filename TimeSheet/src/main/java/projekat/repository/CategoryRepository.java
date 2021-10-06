package projekat.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	//Collection<Category> findByCategoryNameContainingIgnoreCase(String categoryName);
}
