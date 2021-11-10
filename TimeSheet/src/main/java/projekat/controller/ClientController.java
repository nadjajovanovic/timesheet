package projekat.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import projekat.api.api.ClientsApi;
import projekat.api.model.ClientDTO;
import projekat.models.Client;
import projekat.services.ClientService;
import projekat.mapper.ClientMapper;

@RestController
public class ClientController implements ClientsApi {

	@Autowired
	private ClientService clientService;
	
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}
	
	@Override
	public ResponseEntity<List<ClientDTO>> getClients() {
		final var clients = clientService.getAll()
				.stream()
				.map(ClientMapper::toClientDTO)
				.toList();
		return new ResponseEntity(clients, HttpStatus.OK);
	}
	
	@GetMapping("/client/{clientid}")
	public ResponseEntity<Client> getClient(@PathVariable Integer clientid) {
		final var oneClient = clientService.getOne(clientid);
		if (oneClient.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return  new ResponseEntity<>(oneClient.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("client")
	public ResponseEntity<Client> insertClient(@RequestBody Client client) {
		if (client.getClientname() == null || client.getClientname().trim().equals("")
				|| client.getClientid() != null) {
			return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var inserted = clientService.insert(client);
		return new ResponseEntity<>(inserted, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("client")
	public ResponseEntity<Client> updateClient (@RequestBody Client client) {
		if (client.getClientname() == null || client.getClientname().trim().equals("")
				|| client.getClientid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updated = clientService.update(client);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);

	}
	
	@CrossOrigin
	@DeleteMapping("client/{clientid}")
	public ResponseEntity<Client> deleteClient(@PathVariable Integer clientid) {
		final var deleted = clientService.delete(clientid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("client/filter")
	public ResponseEntity<Collection<Client>> filterClientByName(@Param("keyword") String keyword) {
		final var filteredClients = clientService.filterByName(keyword);
		return  new ResponseEntity<>(filteredClients, HttpStatus.OK);
	}

}
