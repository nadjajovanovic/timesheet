package projekat.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import projekat.TimeSheetApplication;
import projekat.api.model.CountryDTO;
import projekat.api.model.TeamMemberDTO;
import projekat.enums.ErrorCode;
import projekat.exception.ErrorResponse;
import projekat.models.Country;
import projekat.models.Teammember;
import projekat.repository.CountryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import projekat.repository.TeamMemberRepository;
import projekat.util.AuthFactory;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CountryControllerIntegrationTest extends BaseUT{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CountryRepository repository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(){
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void doCleanDataBase() {
        cleanDataBase();
    }

    @Test
    void getAllCountries() throws Exception {
        //Arrange
        final var firstCountryName = "Romania";
        final var secondCountryName = "Spain";
        saveTestCountry(firstCountryName);
        saveTestCountry(secondCountryName);
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
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
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/country/{id}", inserted.getCountryid())
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
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
        final var teamMemberName = "John";
        final var teamMember = saveTeamMember(teamMemberName);

        //Act
        //Act
        final var response = mvc.perform(get("/country/{id}", countryId)
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(), teamMember.getPassword()))
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
        final var teamMemberHours = 3;
        final var teamMember = new TeamMemberDTO();
        teamMember.setUsername("username");
        teamMember.setEmail("test@example.com");
        teamMember.setPassword("password");
        teamMember.setRepeatedPassword("password");
        teamMember.setHoursPerWeek(BigDecimal.valueOf(teamMemberHours));
        final var teamMemberSaved = saveTeamMember("John");

        // Act
        final var response = mvc.perform(post("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMemberSaved.getUsername(),teamMemberSaved.getPassword()))
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
    void testCreateCountryBadRequest() throws Exception {
        //Arange
        final var country = new CountryDTO();
        country.setName("");
        final var teamMember = new TeamMemberDTO();
        teamMember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        final var teamMemberSaved = saveTeamMember("John");

        // Act
        final var response = mvc.perform(post("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMemberSaved.getUsername(),teamMemberSaved.getPassword()))
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
        final var teamMember = saveTeamMember("John");

        // Act
        final var response = mvc.perform(post("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
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
        final var teamMember = saveTeamMember("John");

        // Act
        final var response = mvc.perform(post("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
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
        final var teamMemberName = "John";
        final var insertedTeamMember = saveTeamMember(teamMemberName);
        insertedTeamMember.setUsername("username");
        insertedTeamMember.setEmail("test@example.com");
        insertedTeamMember.setPassword("password");

        // Act
        final var response = mvc.perform(put("/country")
                        .header("Authorization", AuthFactory.createAuth(insertedTeamMember.getUsername(),insertedTeamMember.getPassword()))
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
    void testUpdateCountryBadRequest() throws Exception {
        //Arange
        final var countryName = "Serbia";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "";
        inserted.setCountryname(updatedName);
        final var teamMemberName = "John";
        final var insertedTeamMember = saveTeamMember(teamMemberName);

        // Act
        final var response = mvc.perform(put("/country")
                        .header("Authorization", AuthFactory.createAuth(insertedTeamMember.getUsername(),insertedTeamMember.getPassword()))
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
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(put("/country")
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
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
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(delete("/country/{id}", inserted.getCountryid())
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 99;
        final var teamMember = saveTeamMember("John");

        //Act
        final var response = mvc.perform(delete("/country/{id}", countryId)
                        .header("Authorization", AuthFactory.createAuth(teamMember.getUsername(),teamMember.getPassword()))
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
