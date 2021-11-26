package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import projekat.TimeSheetApplication;
import projekat.api.model.ClientDTO;
import projekat.api.model.TeamMemberDTO;
import projekat.enums.ErrorCode;
import projekat.exception.ErrorResponse;
import projekat.mapper.ClientMapper;
import projekat.models.Client;
import projekat.models.Teammember;
import projekat.repository.ClientRepository;
import projekat.repository.TeamMemberRepository;
import projekat.util.AuthFactory;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ClientControllerIntegrationTest extends BaseUT{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientRepository repository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

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
        final var firstClientName = "Jane Doe";
        saveTestClient(firstClientName);
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var clients = Arrays.asList(ResponseReader.readResponse(response, ClientDTO[].class));

        //Assert
        assertEquals(1, clients.size());
        assertEquals(firstClientName, clients.get(0).getName());
    }

    @Test
    void getOneClient() throws Exception {
        //Arrange
        final var clientName = "Jane Doe";
        final var inserted = saveTestClient(clientName);
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/client/{clientid}", inserted.getClientid())
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var client = ResponseReader.readResponse(response, ClientDTO.class);

        //Assert
        assertEquals(clientName, client.getName());
        assertEquals(inserted.getClientid(), client.getId());
    }

    @Test
    void getOneClientNotFound() throws Exception{
        //Arrange
        final var clientId = "100";
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/client/{clientid}", clientId)
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void testCreateClient() throws Exception {
        //Arrange
        final var clientName = "Jane Doe";
        final var client = new ClientDTO();
        client.setName(clientName);
        final var teamMemberName = "John";
        final var teamMemberHours = 3;
        final var teamMember = new TeamMemberDTO();
        teamMember.setName(teamMemberName);
        teamMember.setUsername("username");
        teamMember.setEmail("test@example.com");
        teamMember.setPassword("password");
        teamMember.setRepeatedPassword("password");
        teamMember.setHoursPerWeek(BigDecimal.valueOf(teamMemberHours));
        final var teamMemberSaved = saveTeamMember("John");

        //Act
        final var response = mvc.perform(post("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMemberSaved.getUsername(),teamMemberSaved.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseClient = ResponseReader.readResponse(response, ClientDTO.class);

        //Assert
        assertNotNull(responseClient.getId());
        assertEquals(clientName, responseClient.getName());
    }

    @Test
    void testCreateClientBadRequest() throws Exception {
        //Arange
        final var client = new ClientDTO();
        client.setName("");
        final var teamMember = new TeamMemberDTO();
        teamMember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        teamMember.setName("");
        final var teamMemberSaved = saveTeamMember("John");

        //Act
        final var response = mvc.perform(post("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMemberSaved.getUsername(),teamMemberSaved.getPassword()))
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
        final var client = new ClientDTO();
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(post("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
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
        final var clientName = "Jane Doe";
        final var client = new ClientDTO();
        client.setName(clientName);
        client.setId(5);
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(post("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateClient() throws Exception {
        //Arange
        final var clientName = "Jane Doe";
        final var inserted = saveTestClient(clientName);
        final var updateName = "Jane A Doe";
        final var teamMemberName = "John";
        final var insertedTeamMember = saveTeamMember(teamMemberName);
        insertedTeamMember.setUsername("username");
        insertedTeamMember.setEmail("test@example.com");
        insertedTeamMember.setPassword("password");

        //Act
        final var response = mvc.perform(put("/client")
                        .header("Authorization", AuthFactory.createAuth(insertedTeamMember.getUsername(),insertedTeamMember.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTestClientDTO(inserted, updateName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reponseClient = ResponseReader.readResponse(response, ClientDTO.class);

        assertNotNull(reponseClient.getId());
        assertEquals(updateName, reponseClient.getName());
    }

    @Test
    void testUpdateClientBadRequest() throws Exception {
        //Arange
        final var clientName = "Jane Doe";
        final var inserted = saveTestClient(clientName);
        final var updateName = "";
        inserted.setClientname(updateName);
        final var teamMemberName = "John";
        final var insertedTeamMember = saveTeamMember(teamMemberName);


        //Act
        final var response = mvc.perform(put("/client")
                        .header("Authorization", AuthFactory.createAuth(insertedTeamMember.getUsername(),insertedTeamMember.getPassword()))
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
        final var client = new ClientDTO();
        client.setName("Jane Doe");
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(put("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateClientWrongId() throws Exception {
        //Arrange
        final var clientName = "Jane Doe";
        final var inserted = saveTestClient(clientName);
        inserted.setClientid(9999);
        final var teamMember = saveTeamMember("John");

        // Act
        final var response = mvc.perform(put("/client")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientMapper.toClientDTO(inserted)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void deleteClient() throws Exception {
        //Arange
        final var clientName = "Jane Doe";
        final var inserted = saveTestClient(clientName);
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", inserted.getClientid())
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteClientNotFound() throws Exception {
        //Arrange
        final var clientId = "100";
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(delete("/client/{clientid}", clientId)
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void filterClientsTest() throws Exception {
        //Arange
        final var firstClientName = "Jane Doe";
        final var secondClientName = "John Doe";
        final var thirdClientName = "Dave Doe";
        final var fourthClientName = "Anne Doe";
        saveTestClient(firstClientName);
        saveTestClient(secondClientName);
        saveTestClient(thirdClientName);
        saveTestClient(fourthClientName);
        final var paramName = "keyword";
        final var paramValue = "j";

        //act
        final var response = mvc.perform(get("/client/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredClients = Arrays.asList(ResponseReader.readResponse(response, Client[].class));

        //Assert
        assertEquals(2, filteredClients.size());
        assertEquals(firstClientName, filteredClients.get(0).getClientname());
        assertEquals(secondClientName, filteredClients.get(1).getClientname());
    }

    @Test
    void filterClientsEmptyTest() throws Exception {
        // Arrange
        final var firstClientName = "Jane";
        final var secondClientName = "John";
        saveTestClient(firstClientName);
        saveTestClient(secondClientName);
        final var paramName = "keyword";
        final var paramValue = "Doe";

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

    private Client saveTestClient(String clientName) {
        final var client = createTestClient(clientName);
        return repository.saveAndFlush(client);
    }

    private ClientDTO saveTestClientDTO(Client c, String clientName) {
        final var client = new ClientDTO();
        client.setId(c.getClientid());
        client.setName(clientName);
        return client;
    }

    private Teammember saveTeamMember(String teammemberName) {
        final var teammember = new Teammember();
        teammember.setTeammembername(teammemberName);
        teammember.setPassword("password");
        teammember.setUsername("username");
        teammember.setEmail("test@example.com");
        teammember.setHoursperweek(2.3);
        teammember.setStatus(true);
        return teamMemberRepository.saveAndFlush(teammember);
    }

    private TeamMemberDTO saveTeamMemberDTO(Teammember t, String teammemberName) {
        final var teammember = new TeamMemberDTO();
        teammember.setId(t.getTeammemberid());
        teammember.setName(teammemberName);
        teammember.setUsername(t.getUsername());
        teammember.setEmail(t.getEmail());
        teammember.setPassword(t.getPassword());
        teammember.setStatus(t.getStatus());
        teammember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        teammember.setRepeatedPassword(t.getPassword());
        teammember.setStatus(true);
        return teammember;
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
