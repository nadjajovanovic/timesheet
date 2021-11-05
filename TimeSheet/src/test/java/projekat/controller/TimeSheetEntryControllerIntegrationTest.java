package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import projekat.TimeSheetApplication;
import projekat.models.Category;
import projekat.models.Client;
import projekat.models.Project;
import projekat.models.TimeSheetEntry;
import projekat.repository.CategoryRepository;
import projekat.repository.ClientRepository;
import projekat.repository.ProjectRepository;
import projekat.repository.TimeSheetEntryRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TimeSheetEntryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TimeSheetEntryRepository entryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void doCleanDataBase() {
        cleanDataBase();
    }

    @Test
    public void getAllEntries() throws Exception {
        //Arrange
        final var firstEntryDescription = "FirstEntryDescription";
        final var secondEntryDescription = "SecondEntryDescription";
        createTestEntry(firstEntryDescription);
        createTestEntry(secondEntryDescription);

         //Act
        final var response = mvc.perform(get("/entry")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var entries = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //Assert
        assertEquals(entries.size(), 2);
        assertEquals(entries.get(0).getDescription(), firstEntryDescription);
        assertEquals(entries.get(1).getDescription(), secondEntryDescription);
    }

    @Test
    public void getOneEntry() throws Exception {
        //Arrange
        final var entryDescription = "FirstEntry";
        final var inserted = createTestEntry(entryDescription);

        //Act
        final var response = mvc.perform(get("/entry/{id}", inserted.getEntryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var entry = ResponseReader.readResponse(response, TimeSheetEntry.class);

        //Assert
        assertEquals(entry.getDescription(), entryDescription);
        assertEquals(entry.getEntryId(), inserted.getEntryId());
    }

    @Test
    public void  getOneEntryNotFound() throws Exception {
        //Arrange
        final var entryId = 100;

        //Act
        final var response = mvc.perform(get("/entry/{id}", entryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void  testCreateEntry() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        createTestCategory("da");
        final var category = createTestCategory("test category");
        final var client = createTestClient("Steve");
        final var project = createTestProject("Weather App", "Description");
        entry.setCategory(category);
        entry.setClient(client);
        entry.setProject(project);
        entry.setTime(4.5);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseEntry = ResponseReader.readResponse(response, TimeSheetEntry.class);

        // Assert
        assertNotNull(responseEntry.getEntryId());
        assertEquals(responseEntry.getCategory().getCategoryid(), category.getCategoryid());
        assertEquals(responseEntry.getClient().getClientid(), client.getClientid());
        assertEquals(responseEntry.getProject().getProjectid(), project.getProjectid());
    }

    @Test
    public void  testCreateEntryBadRequestNoProject() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var category = createTestCategory("test category");
        final var client = createTestClient("Steve");
        entry.setCategory(category);
        entry.setClient(client);
        entry.setProject(null);
        entry.setTime(4.5);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateEntryBadRequestNoClient() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var category = createTestCategory("test category");
        final var project = createTestProject("Test project", "test description");
        entry.setCategory(category);
        entry.setClient(null);
        entry.setProject(project);
        entry.setTime(4.5);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateEntryBadRequestNoCategory() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var client = createTestClient("test client");
        final var project = createTestProject("Test project", "test description");
        entry.setCategory(null);
        entry.setClient(client);
        entry.setProject(project);
        entry.setTime(4.5);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateEntryBadRequestCategoryNotFound() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var category = createTestCategory("test category");
        final var client = createTestClient("test client");
        final var project = createTestProject("Test project", "test description");
        category.setCategoryid(456);
        entry.setCategory(category);
        entry.setClient(client);
        entry.setProject(project);
        entry.setTime(4.5);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void  testCreateEntryBadRequestTimeLessThatZero() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var category = createTestCategory("test category");
        final var client = createTestClient("test client");
        final var project = createTestProject("Test project", "test description");
        entry.setCategory(category);
        entry.setClient(client);
        entry.setProject(project);
        entry.setTime(-2.3);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testUpdateEntry() throws Exception {
        //Arrange
        final var description = "5h total";
        final var entry = createTestEntry(description);
        final var updatedDescription = "7.5h total";
        entry.setDescription(updatedDescription);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseEntry = ResponseReader.readResponse(response, TimeSheetEntry.class);

        // Assert
        assertNotNull(responseEntry);
        assertEquals(responseEntry.getDescription(), updatedDescription);
    }

    @Test
    public void  testUpdateEntryBadRequest() throws Exception {
        //Arrange
        final var description = "Work day";
        final var entry = createTestEntry(description);
        entry.setTime(25.5);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testUpdateEntryNotFound() throws Exception {
        //Arrange
        final var description = "Demo project";
        final var entry = createTestEntry(description);
        entry.setEntryId(2345);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void deleteEntry() throws Exception {
        //Arrange
        final var entryDescription = "FirstEntry";
        final var inserted = createTestEntry(entryDescription);

        //Act
        final var response = mvc.perform(delete("/entry/{id}", inserted.getEntryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteEntryNotFound() throws Exception {
        //Arrange
        final var entryId = 100;

        //Act
        final var response = mvc.perform(delete("/entry/{id}", entryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    private TimeSheetEntry createTestEntry(String description) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        final var category = categoryRepository.saveAndFlush(new Category());
        entry.setCategory(category);
        final var client = clientRepository.saveAndFlush(new Client());
        entry.setClient(client);
        final var project = projectRepository.saveAndFlush(new Project());
        entry.setProject(project);
        entry.setTime(3.5);
        return entryRepository.saveAndFlush(entry);
    }

    private Category createTestCategory(String categoryName) {
        final var category = new Category();
        category.setCategoryname(categoryName);
        return categoryRepository.saveAndFlush(category);
    }

    private Client createTestClient(String clientName) {
        final var client = new Client();
        client.setClientname(clientName);
        return clientRepository.saveAndFlush(client);
    }

    private Project createTestProject(String projectName, String projectDescription) {
        final var project = new Project();
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);
        return projectRepository.saveAndFlush(project);
    }

    private void cleanDataBase() {
        entryRepository.deleteAll();
        entryRepository.flush();
    }
}
