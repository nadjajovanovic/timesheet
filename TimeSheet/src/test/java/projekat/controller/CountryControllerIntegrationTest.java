package projekat.controller;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
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
import java.util.List;

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
        final String firstCountryName = "Romania";
        final String secondCountryName = "Spain";
        createTestCountry(firstCountryName);
        createTestCountry(secondCountryName);

        //Act
        final MvcResult response = mvc.perform(get("/country")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final List<Country> countries = Arrays.asList(ResponseReader.readResponse(response, Country[].class));

        //Assert
        assertEquals(countries.size(), 2);
        assertEquals(countries.get(0).getCountryname(), firstCountryName);
        assertEquals(countries.get(1).getCountryname(), secondCountryName);
    }

    @Test
    public void getOneCountry() throws Exception {
        //Arrange
        final String countryName = "Romania";
        final Country inserted = createTestCountry(countryName);

        //Act
        final MvcResult response = mvc.perform(get("/country/{id}", inserted.getCountryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final Country country = ResponseReader.readResponse(response, Country.class);

        //Assert
        assertEquals(country.getCountryname(), countryName);
        assertEquals(country.getCountryid(), inserted.getCountryid());
    }

    @Test
    public void  getOneCountryNotFound() throws Exception {
        //Arrange

        //Act
        final MvcResult response = mvc.perform(get("/country/{id}", 432))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    public void  testCreateCountry() throws Exception {
        //Arange
        final String countryName = "Spain";
        final Country country = new Country();
        country.setCountryname(countryName);

        // Act
        final MvcResult response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final Country responseCountry = ResponseReader.readResponse(response, Country.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(responseCountry.getCountryname(), countryName);
    }

    @Test
    public void  testCreateCountryBadRequest() throws Exception {
        //Arange
        final Country country = new Country();
        country.setCountryname("   ");

        // Act
        final MvcResult response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testCreateCountryNameNotExist() throws Exception {
        //Arange
        final Country country = new Country();

        // Act
        final MvcResult response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testCreateCountryIdExists() throws Exception {
        //Arange
        final String countryName = "Greece";
        final Country country = new Country();
        country.setCountryname(countryName);
        country.setCountryid(123);

        // Act
        final MvcResult response = mvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testUpdateCountry() throws Exception {

        //Arange
        final String countryName = "Itly";
        final Country insertedCountry = createTestCountry(countryName);
        final String updatedName = "Italy";
        insertedCountry.setCountryname(updatedName);

        // Act
        final MvcResult response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedCountry))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final Country responseCountry = ResponseReader.readResponse(response, Country.class);

        // Assert
        assertNotNull(responseCountry.getCountryid());
        assertEquals(responseCountry.getCountryname(), updatedName);
    }

    @Test
    public void  testUpdateCountryBadRequest() throws Exception {
        //Arange
        final String countryName = "nameForInsert";
        final Country insertedCountry = createTestCountry(countryName);
        final String updatedName = "   ";
        insertedCountry.setCountryname(updatedName);

        // Act
        final MvcResult response = mvc.perform(put("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedCountry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void deleteCountry() throws Exception {
        //Arrange
        final String countryName = "Italy";
        final Country insertedCountry = createTestCountry(countryName);

        //Act
        final MvcResult response = mvc.perform(delete("/country/{id}", insertedCountry.getCountryid()))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 200);
    }

    @Test
    public void deleteCountryNotFound() throws Exception {
        //Arrange

        //Act
        final MvcResult response = mvc.perform(delete("/country/{id}", 99))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);
    }

    private Country createTestCountry(String countryName) {
        final Country country = new Country();
        country.setCountryname(countryName);
        return repository.saveAndFlush(country);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
