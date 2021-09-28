package projekat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Client;
import projekat.repository.ClientRepository;

@RestController
public class ClientController {

	@Autowired(required = false)
	private ClientRepository clientRepository;
	
	@GetMapping(value = "client")
	public List<Client> getClients() {
		return clientRepository.findAll();
	}
	
	@GetMapping("/client/{clientid}")
	public Client getClient(@PathVariable Integer clientid) {
		return clientRepository.getById(clientid);
	}
	
	@GetMapping("/client/{clientName}")
	public List<Client> findByName(@PathVariable String clientName) {
		return clientRepository.findByNameContainingIgnoreCase(clientName);
	}
	
	@GetMapping("/client/{clientAddress}")
	public List<Client> findByAddress(@PathVariable String clientAddress) {
		return clientRepository.findByAddressContainingIgnoreCase(clientAddress);
	}
	
	@PostMapping("client")
	public ResponseEntity<Client> insertClient(@RequestBody Client client) {
		clientRepository.save(client);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("client")
	public ResponseEntity<Client> updateClient (@RequestBody Client client) {
		if (clientRepository.existsById(client.getClientid()))
			clientRepository.save(client);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("client/{clientId}")
	public ResponseEntity<Client> deleteClient(@PathVariable Integer clientId) {
		if (clientRepository.existsById(clientId))
			clientRepository.deleteById(clientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
