package projekat.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer>{

}
