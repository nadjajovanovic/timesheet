package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import projekat.TimeSheetApplication;
import projekat.api.model.ClientDTO;
import projekat.api.model.CountryDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.models.Country;
import projekat.models.Teammember;
import projekat.repository.CountryRepository;
import projekat.repository.TeamMemberRepository;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CountryControllerIntegrationTest extends BaseUT{

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CountryRepository repository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    private final String usernameAdmin = "adminTest";
    private final String usernameWorker = "workerTest";

    private Teammember teammember;

    @BeforeAll
    static void setUp(){
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void doCleanDataBase() {
        cleanDataBase();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        registerUser(usernameAdmin, TeamMemberRoles.ROLE_ADMIN);
        registerUser(usernameWorker, TeamMemberRoles.ROLE_WORKER);
    }

    @Test
    void getAllCountries() throws Exception {
        //Arrange
        final var firstCountryName = "Romania";
        final var secondCountryName = "Spain";
        saveTestCountry(firstCountryName);
        saveTestCountry(secondCountryName);

        //Act
        final var response = mvc.perform(get("/country")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var countries = Arrays.asList(ResponseReader.readResponse(response, CountryDTO[].class));

        //Assert
        assertEquals(2, countries.size());
        assertEquals(firstCountryName, countries.get(0).getName());
        assertEquals(secondCountryName, countries.get(1).getName());
    }

    @Test
    void getOneCountry() throws Exception {
        //Arrange
        final var countryName = "Romania";
        final var inserted = saveTestCountry(countryName);

        //Act
        final var response = mvc.perform(get("/country/{id}", inserted.getCountryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var country = ResponseReader.readResponse(response, CountryDTO.class);

        //Assert
        assertEquals(countryName, country.getName());
        assertEquals(inserted.getCountryid(), country.getCountryid());
    }

    @Test
    void getOneCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 123;

        //Act
        final var response = mvc.perform(get("/country/{id}", countryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    @Test
    void testCreateCountry() throws Exception {
        //Arange
        final var countryName = "Spain";
        final var country = new CountryDTO();
        country.setName(countryName);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseCountry = ResponseReader.readResponse(response, CountryDTO.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(countryName, responseCountry.getName());
    }

    @Test
    void testCreateCountryForbidden() throws Exception {
        //Arrange
        final var countryName = "Spain";
        final var country = new CountryDTO();
        country.setName(countryName);
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var responseClient = ResponseReader.readResponse(response, ErrorResponse.class);

        //assert
        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseClient.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseClient.getErrorCode());
    }

    @Test
    void testCreateCountryBadRequest() throws Exception {
        //Arange
        final var country = new CountryDTO();
        country.setName("");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateCountryNameNotExist() throws Exception {
        //Arange
        final var country = new CountryDTO();
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateCountryIdExists() throws Exception {
        //Arrange
        final var countryName = "Greece";
        final var country = new CountryDTO();
        country.setName(countryName);
        country.setCountryid(5);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();
        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateCountry() throws Exception {
        //Arrange
        final var countryName = "Itly";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "Italy";
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTestCountryDTO(inserted, updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseCountry = ResponseReader.readResponse(response, CountryDTO.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(updatedName, responseCountry.getName());
    }

    @Test
    void testUpdateCountryForbidden() throws Exception {
        //Arrange
        final var countryName = "Itly";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "Italy";
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTestCountryDTO(inserted, updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), error.getErrorCode());
    }

    @Test
    void testUpdateCountryBadRequest() throws Exception {
        //Arange
        final var countryName = "Serbia";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "";
        testAuthFactory.loginUser(usernameAdmin);

        inserted.setCountryname(updatedName);

        // Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateCountryNoId() throws Exception {
        //Arange
        final var country = new CountryDTO();
        country.setName("United States of America");
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void deleteCountry() throws Exception {
        //Arrange
        final var countryName = "Italy";
        final var inserted = saveTestCountry(countryName);
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/country/{id}", inserted.getCountryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCountryForbidden() throws Exception {
        //Arrange
        final var clientName = "Jane Doe";
        final var inserted = saveTestCountry(clientName);
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(delete("/country/{countryid}", inserted.getCountryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), error.getErrorCode());
    }

    @Test
    void deleteCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 99;
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/country/{id}", countryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var error =  ResponseReader.readResponse(response, ErrorResponse.class);

        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    private Country saveTestCountry(String countryName) {
        final var country = createTestCountry(countryName);
        return repository.saveAndFlush(country);
    }

    private CountryDTO saveTestCountryDTO (Country c, String countryName) {
        final var country = new CountryDTO();
        country.setCountryid(c.getCountryid());
        country.setName(countryName);
        return country;
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
        teamMemberRepository.deleteAll();
        teamMemberRepository.flush();
    }
}
