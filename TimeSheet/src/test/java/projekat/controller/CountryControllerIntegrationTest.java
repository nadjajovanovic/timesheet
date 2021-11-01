package projekat.controller;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import projekat.TimeSheetApplication;
import projekat.models.Country;
import projekat.repository.CountryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CountryControllerIntegrationTest {

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
    public void doCleanDataBase() {
        cleanDataBase();
    }

    @Test
    public void getAllCountries() throws Exception {
        //Arrange
        final var firstCountryName = "Romania";
        final var secondCountryName = "Spain";
        createTestCountry(firstCountryName);
        createTestCountry(secondCountryName);

        //Act
        final var response = mvc.perform(get("/country")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var countries = Arrays.asList(ResponseReader.readResponse(response, Country[].class));

        //Assert
        assertEquals(countries.size(), 2);
        assertEquals(countries.get(0).getCountryname(), firstCountryName);
        assertEquals(countries.get(1).getCountryname(), secondCountryName);
    }

    @Test
    public void getOneCountry() throws Exception {
        //Arrange
        final var countryName = "Romania";
        final var inserted = createTestCountry(countryName);

        //Act
        final var response = mvc.perform(get("/country/{id}", inserted.getCountryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var country = ResponseReader.readResponse(response, Country.class);

        //Assert
        assertEquals(country.getCountryname(), countryName);
        assertEquals(country.getCountryid(), inserted.getCountryid());
    }

    @Test
    public void  getOneCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 123;

        //Act
        final var response = mvc.perform(get("/country/{id}", countryId))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void  testCreateCountry() throws Exception {
        //Arange
        final var countryName = "Spain";
        final var country = new Country();
        country.setCountryname(countryName);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseCountry = ResponseReader.readResponse(response, Country.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(responseCountry.getCountryname(), countryName);
    }

    @Test
    public void  testCreateCountryBadRequest() throws Exception {
        //Arange
        final var country = new Country();
        country.setCountryname("   ");

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateCountryNameNotExist() throws Exception {
        //Arange
        final var country = new Country();

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateCountryIdExists() throws Exception {
        //Arrange
        final var countryName = "Greece";
        final var country = new Country();
        country.setCountryname(countryName);
        country.setCountryid(123);

        // Act
        final var response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testUpdateCountry() throws Exception {

        //Arrange
        final var countryName = "Itly";
        final var insertedCountry = createTestCountry(countryName);
        final var updatedName = "Italy";
        insertedCountry.setCountryname(updatedName);

        // Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedCountry))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseCountry = ResponseReader.readResponse(response, Country.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(responseCountry.getCountryname(), updatedName);
    }

    @Test
    public void  testUpdateCountryBadRequest() throws Exception {
        //Arange
        final var countryName = "nameForInsert";
        final var insertedCountry = createTestCountry(countryName);
        final var updatedName = "   ";
        insertedCountry.setCountryname(updatedName);

        // Act
        final var response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedCountry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteCountry() throws Exception {
        //Arrange
        final var countryName = "Italy";
        final var insertedCountry = createTestCountry(countryName);

        //Act
        final var response = mvc.perform(delete("/country/{id}", insertedCountry.getCountryid()))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteCountryNotFound() throws Exception {
        //Arrange
        final var countryId = 99;
        //Act
        final var response = mvc.perform(delete("/country/{id}", countryId))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    private Country createTestCountry(String countryName) {
        final var country = new Country();
        country.setCountryname(countryName);
        return repository.saveAndFlush(country);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
