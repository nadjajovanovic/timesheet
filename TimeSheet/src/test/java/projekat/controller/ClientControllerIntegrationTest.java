package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import projekat.TimeSheetApplication;
import projekat.models.Client;
import projekat.repository.ClientRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientRepository repository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void doCleanDatabase() {
        cleanDataBase();
    }

    @Test
    void getAllClients() throws Exception {
        //Arrange
        final var firstClientName = "First";
        createTestClient(firstClientName);

        //Act
        final var response = mvc.perform(get("/client")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var clients = Arrays.asList(ResponseReader.readResponse(response, Client[].class));

        //Assert
        assertEquals(1, clients.size());
        assertEquals(firstClientName, clients.get(0).getClientname());
    }

    @Test
    void getOneClient() throws Exception {
        //Arrange
        final var clientName = "First";
        final var inserted = createTestClient(clientName);

        //Act
        final var response = mvc.perform(get("/client/{clientid}", inserted.getClientid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var client = ResponseReader.readResponse(response, Client.class);

        //Assert
        assertEquals(clientName, client.getClientname());
        assertEquals(inserted.getClientid(), client.getClientid());
    }

    @Test
    void getOneClientNotFound() throws Exception{
        //Arrange
        final var clientId = "100";

        //Act
        final var response = mvc.perform(get("/client/{clientid}", clientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateClient() throws Exception {
        //Arrange
        final var clientName = "Please insert me";
        final var client = new Client();
        client.setClientname(clientName);

        //Act
        final var response = mvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseClient = ResponseReader.readResponse(response, Client.class);

        //Assert
        assertNotNull(responseClient.getClientid());
        assertEquals(clientName, responseClient.getClientname());
    }

    @Test
    void testCreateClientBadRequest() throws Exception {
        //Arange
        final var client = new Client();
        client.setClientname("");

        //Act
        final var response = mvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateClientNameNotExist() throws Exception {
        //Arange
        final var client = new Client();

        //Act
        final var response = mvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateClientIdExists() throws Exception {
        //Arange
        final var clientName = "Please insert me";
        final var client = new Client();
        client.setClientname(clientName);
        client.setClientid(5);

        //Act
        final var response = mvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateClient() throws Exception {
        //Arange
        final var clientName = "nameForInsert";
        final var inserted = createTestClient(clientName);
        final var updateName = "nameForUpdate";
        inserted.setClientname(updateName);

        //Act
        final var response = mvc.perform(put("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reponseClient = ResponseReader.readResponse(response, Client.class);

        assertNotNull(reponseClient.getClientid());
        assertEquals(updateName, reponseClient.getClientname());
    }

    @Test
    void testUpdateClientBadRequest() throws Exception {
        //Arange
        final var clientName = "nameForInsert";
        final var inserted = createTestClient(clientName);
        final var updateName = "";
        inserted.setClientname(updateName);

        //Act
        final var response = mvc.perform(put("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateClientNoId() throws Exception {
        //Arange
        final var client = new Client();
        client.setClientname("Not important");

        //Act
        final var response = mvc.perform(put("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteClient() throws Exception {
        //Arange
        final var clientName = "Delete Me";
        final var inserted = createTestClient(clientName);

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", inserted.getClientid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteClientNotFound() throws Exception {
        //Arrange
        final var clientId = "100";

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", clientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void filterClientsTest() throws Exception {
        //Arange
        final var firstClientName = "Nadja";
        final var secondClientName = "Nikola";
        final var thirdClientName = "Nikolina";
        final var fourthClientName = "Bojana";
        createTestClient(firstClientName);
        createTestClient(secondClientName);
        createTestClient(thirdClientName);
        createTestClient(fourthClientName);
        final var paramName = "keyword";
        final var paramValue = "n";

        //act
        final var response = mvc.perform(get("/client/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredClients = Arrays.asList(ResponseReader.readResponse(response, Client[].class));

        //Assert
        assertEquals(3, filteredClients.size());
        assertEquals(firstClientName, filteredClients.get(0).getClientname());
        assertEquals(secondClientName, filteredClients.get(1).getClientname());
        assertEquals(thirdClientName, filteredClients.get(2).getClientname());
    }

    @Test
    void filterClientsEmptyTest() throws Exception {
        // Arrange
        final var firstClientName = "Bojana";
        final var secondClientName = "Tijana";
        createTestClient(firstClientName);
        createTestClient(secondClientName);
        final var paramName = "keyword";
        final var paramValue = "Nadja";

        // Act
        final var response = mvc.perform(get("/client/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredClients = Arrays.asList(ResponseReader.readResponse(response, Client[].class));

        // Assert
        assertEquals(0, filteredClients.size());
    }

    private Client createTestClient(String clientName) {
        final var client = new Client();
        client.setClientname(clientName);
        return repository.saveAndFlush(client);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
