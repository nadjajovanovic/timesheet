package projekat.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import projekat.TimeSheetApplication;
import projekat.api.model.CountryDTO;
import projekat.enums.ErrorCode;
import projekat.exception.ErrorResponse;
import projekat.models.Country;
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
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

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
        final var response = mvc.perform(get("/country/{id}", countryId))
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
    void testCreateCountryBadRequest() throws Exception {
        //Arange
        final var country = new CountryDTO();
        country.setName("");

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
        country.setCountryid(123);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.ID_EXISTS.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateCountry() throws Exception {
        //Arrange
        final var countryName = "Itly";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "Italy";

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
    void testUpdateCountryBadRequest() throws Exception {
        //Arange
        final var countryName = "Serbia";
        final var inserted = saveTestCountry(countryName);
        final var updatedName = "";
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

        //Act
        final var response = mvc.perform(put("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.ID_NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void deleteCountry() throws Exception {
        //Arrange
        final var countryName = "Italy";
        final var inserted = saveTestCountry(countryName);

        //Act
        final var response = mvc.perform(delete("/country/{id}", inserted.getCountryid()))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 99;
        //Act
        final var response = mvc.perform(delete("/country/{id}", countryId))
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
    }
}
