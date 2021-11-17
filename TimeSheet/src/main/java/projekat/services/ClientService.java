package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
import projekat.exception.NotFoundException;
import projekat.exception.InputFieldException;
import projekat.models.Client;
import projekat.repository.ClientRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Collection<Client> getAll() {
        final var clients = clientRepository.findAll();
        return clients;
    }

    public Optional<Client> getOne(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var oneClient = clientRepository.findById(id);
        return oneClient;
    }

    public Client insert(Client client) {
        if (client.getClientid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }
        final var inserted = clientRepository.save(client);
        return inserted;
    }

    public Client update(Client client) {

        if (client.getClientid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.NOT_FOUND);
        }
        if(!clientRepository.existsById(client.getClientid())) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", client.getClientid()),HttpStatus.NOT_FOUND);
        }
        final var updated = clientRepository.save(client);
        return updated;
    }

    public boolean delete(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        clientRepository.deleteById(id);
        return true;
    }

    public Collection<Client> filterByName(String keyword){
        final var allClients = clientRepository.findAll();
        final var filteredClients = allClients.stream()
                                                .filter(e -> e.getClientname()
                                                        .toLowerCase()
                                                        .startsWith(keyword.toLowerCase()))
                                                .toList();

        return filteredClients;
    }
}
