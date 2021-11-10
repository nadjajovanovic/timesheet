package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        final var oneClient = clientRepository.findById(id);
        return oneClient;
    }

    public Client insert(Client client) {
        final var inserted = clientRepository.save(client);
        return inserted;
    }

    public Client update(Client client) {
        if(!clientRepository.existsById(client.getClientid()))
            return null;
        final var updated = clientRepository.save(client);
        return updated;
    }

    public boolean delete(Integer id) {
        if (!clientRepository.existsById(id))
            return false;
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
