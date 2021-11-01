package projekat.controller;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	@Autowired
	private ClientRepository clientRepository;
	
	public ClientController(ClientRepository clientRepository) {

		this.clientRepository = clientRepository;
	}
	
	@GetMapping(value = "client")
	public Collection<Client> getClients() {
		return clientRepository.findAll();
	}
	
	@GetMapping("/client/{clientid}")
	public ResponseEntity<Client> getClient(@PathVariable Integer clientid) {
		Optional<Client> client = clientRepository.findById(clientid);
		if (!client.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return  new ResponseEntity<>(client.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("client")
	public ResponseEntity<Client> insertClient(@RequestBody Client client) {
		if (client.getClientname() == null || client.getClientname().trim().equals("")
				|| client.getClientid() != null) {
			return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Client clie = clientRepository.save(client);
		return new ResponseEntity<Client>(clie, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("client")
	public ResponseEntity<Client> updateClient (@RequestBody Client client) {
		if (!clientRepository.existsById(client.getClientid())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (client.getClientname() == null || client.getClientname().trim().equals("")
				|| client.getClientid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Client inserted = clientRepository.save(client);
		return new ResponseEntity<>(inserted, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("client/{clientid}")
	public ResponseEntity<Client> deleteClient(@PathVariable Integer clientid) {
		if (!clientRepository.existsById(clientid)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		clientRepository.deleteById(clientid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
