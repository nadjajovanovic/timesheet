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
        clientDTO.setCountryid(client.getCountry().getCountryid());
        return clientDTO;
    }
}
