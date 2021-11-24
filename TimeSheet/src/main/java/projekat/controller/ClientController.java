package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projekat.api.api.ClientApi;
import projekat.api.model.ClientDTO;
import projekat.mapper.ClientMapper;
import projekat.models.Client;
import projekat.services.ClientService;

import java.util.Collection;
import java.util.List;

@RestController
public class ClientController implements ClientApi {

	@Autowired
	private final ClientService clientService;

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

	@Override
	@GetMapping("/client/{clientid}")
	public ResponseEntity<ClientDTO> getClient(@PathVariable Integer clientid) {
		final var oneClient = clientService.getOne(clientid);
		return  new ResponseEntity(ClientMapper.toClientDTO(oneClient.get()), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<ClientDTO> insertClient(@RequestBody ClientDTO client) {
		final var inserted = clientService.insert(ClientMapper.toClient(client));
		return new ResponseEntity(ClientMapper.toClientDTO(inserted), HttpStatus.CREATED);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<ClientDTO> updateClient (@RequestBody ClientDTO client) {
		final var updated = clientService.update(ClientMapper.toClient(client));
		return new ResponseEntity(ClientMapper.toClientDTO(updated), HttpStatus.OK);

	}

	@CrossOrigin
	@Override
	public ResponseEntity<ClientDTO> deleteClient(@PathVariable Integer clientid) {
		clientService.delete(clientid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("client/filter")
	public ResponseEntity<Collection<Client>> filterClientByName(@Param("keyword") String keyword) {
		final var filteredClients = clientService.filterByName(keyword);
		return  new ResponseEntity<>(filteredClients, HttpStatus.OK);
	}

}
