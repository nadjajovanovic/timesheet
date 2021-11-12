package projekat.mapper;

import projekat.api.model.ClientDTO;
import projekat.models.Client;

public class ClientMapper {

    public static ClientDTO toClientDTO(Client client) {
        final var clientDTO = new ClientDTO();
        clientDTO.setId(client.getClientid());
        clientDTO.setName(client.getClientname());
        clientDTO.setAddress(client.getClientaddress());
        clientDTO.setCity(client.getClientcity());
        clientDTO.setZipCode(client.getClientzipcode());
        clientDTO.setCountryid(client.getCountryid());
        return clientDTO;
    }

    public static Client toClient(ClientDTO clientDTO) {
        final var client = new Client();
        client.setClientid(clientDTO.getId());
        client.setClientname(clientDTO.getName());
        client.setClientaddress(clientDTO.getAddress());
        client.setClientcity(clientDTO.getCity());
        client.setClientzipcode(clientDTO.getZipCode());
        client.setCountryid(clientDTO.getCountryid());
        return client;
    }
}
