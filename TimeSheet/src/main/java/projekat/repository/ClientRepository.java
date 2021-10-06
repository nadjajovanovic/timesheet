package projekat.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{
	/*List<Client> findByNameContainingIgnoreCase(String clientName);
	List<Client> findByAddressContainingIgnoreCase(String clientAddress);*/
}
