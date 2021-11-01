package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import projekat.TimeSheetApplication;
import projekat.models.Client;
import projekat.repository.ClientRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
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
    public void doCleanDatabase() {
        cleanDataBase();
    }

    @Test
    public void getAllClients() throws Exception {
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
        assertEquals(clients.size(), 1);
        assertEquals(clients.get(0).getClientname(), firstClientName);
    }

    @Test
    public void getOneClient() throws Exception {
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
        assertEquals(client.getClientname(), clientName);
        assertEquals(client.getClientid(), Integer.valueOf(inserted.getClientid()));
    }

    @Test
    public void getOneClientNotFound() throws Exception{
        //Arrange
        final var clientId = "100";

        //Act
        final var response = mvc.perform(get("/client/{clientid}", clientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCreateClient() throws Exception {
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
        assertEquals(responseClient.getClientname(), clientName);
    }

    @Test
    public void testCreateClientBadRequest() throws Exception {
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
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testCreateClientNameNotExist() throws Exception {
        //Arange
        final var client = new Client();

        //Act
        final var response = mvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testCreateClientIdExists() throws Exception {
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
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateClient() throws Exception {
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
        assertEquals(reponseClient.getClientname(), updateName);
    }

    @Test
    public void testUpdateClientBadRequest() throws Exception {
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
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test @Disabled
    public void testUpdateClientNoId() throws Exception {
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
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteClient() throws Exception {
        //Arange
        final var clientName = "Delete Me";
        final var inserted = createTestClient(clientName);

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", inserted.getClientid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteClientNotFound() throws Exception {
        //Arrange
        final var clientId = "100";

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", clientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
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
