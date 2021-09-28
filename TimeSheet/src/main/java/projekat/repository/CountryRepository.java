package projekat.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer>{
	
	List<Country> findByCountryNameContainingIgnoreCase(String countryName);
}
