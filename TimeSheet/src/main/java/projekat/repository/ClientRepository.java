package projekat.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Client;

import java.util.Collection;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{
    Collection<Client> findByClientnameStartingWithIgnoreCase(String name);
}
