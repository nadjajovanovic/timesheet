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
import projekat.api.model.TimeSheetEntryDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.models.*;
import projekat.repository.*;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class TimeSheetEntryControllerIntegrationTest extends BaseUT{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TimeSheetEntryRepository entryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    private final String usernameAdmin = "adminTest";
    private final String usernameWorker = "workerTest";

    private Teammember registeredWorker;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void cleanSetup() {
        cleanDataBase();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

       registeredWorker = registerUser(usernameWorker, TeamMemberRoles.ROLE_WORKER);
       registerUser(usernameAdmin, TeamMemberRoles.ROLE_ADMIN);
    }

    @Test
    void getAllEntries() throws Exception {
        //Arrange
        final var firstEntryDescription = "FirstEntryDescription";
        final var secondEntryDescription = "SecondEntryDescription";
        createTestEntry(firstEntryDescription, registeredWorker.getTeammemberid());
        createTestEntry(secondEntryDescription, registeredWorker.getTeammemberid());
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(get("/entry")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var entries = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryDTO[].class));

        //Assert
        assertEquals(2, entries.size());
        assertEquals(firstEntryDescription, entries.get(0).getDescription());
        assertEquals(secondEntryDescription, entries.get(1).getDescription());
    }

    @Test
    void getOneEntry() throws Exception {
        //Arrange
        final var entryDescription = "FirstEntry";
        final var inserted = createTestEntry(entryDescription, registeredWorker.getTeammemberid());
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(get("/entry/{id}", inserted.getEntryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var entry = ResponseReader.readResponse(response, TimeSheetEntryDTO.class);

        //Assert
        assertEquals(entryDescription, entry.getDescription());
        assertEquals(inserted.getEntryId(), entry.getId());
    }

    @Test
    void getOneEntryNotFound() throws Exception {
        //Arrange
        final var entryId = 100;
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(get("/entry/{id}", entryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    @Test
    void testCreateEntry() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var categoryName = "test category";
        final var category = saveTestCategory(categoryName);
        final var clientName = "Steve";
        final var client = saveTestClient(clientName);
        final var projectName = "Weather App";
        final var projectDescription = "This is Project Description";
        final var project = saveTestProject(projectName, projectDescription);
        entry.setCategoryId(category.getCategoryid());
        entry.setClientId(client.getClientid());
        entry.setProjectId(project.getProjectid());
        entry.setTimeSpent(BigDecimal.valueOf(4.5));
        entry.setDate("2021-11-10");
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseEntry = ResponseReader.readResponse(response, TimeSheetEntryDTO.class);
        final var fetchedEntry = fetchEntry(responseEntry.getId());

        // Assert
        assertNotNull(responseEntry.getId());
        assertEquals(category.getCategoryid(), responseEntry.getCategoryId());
        assertEquals(categoryName, fetchedEntry.get().getCategory().getCategoryname());
        assertEquals(client.getClientid(), responseEntry.getClientId());
        assertEquals(client.getClientname(), fetchedEntry.get().getClient().getClientname());
        assertEquals(project.getProjectid(), responseEntry.getProjectId());
    }

    @Test
    void testCreateEntryBadRequestNoProject() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var category = saveTestCategory("test category");
        final var client = saveTestClient("Steve");
        entry.setCategoryId(category.getCategoryid());
        entry.setClientId(client.getClientid());
        entry.setProjectId(null);
        entry.setTimeSpent(BigDecimal.valueOf(4.5));
        entry.setDate("2021-11-11");
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateEntryBadRequestNoClient() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var category = saveTestCategory("test category");
        final var project = saveTestProject("Test project", "test description");
        entry.setCategoryId(category.getCategoryid());
        entry.setClientId(null);
        entry.setProjectId(project.getProjectid());
        entry.setTimeSpent(BigDecimal.valueOf(4.5));
        entry.setDate("2021-10-10");
        testAuthFactory.loginUser(usernameWorker);


        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateEntryBadRequestNoCategory() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntry();
        final var client = saveTestClient("test client");
        final var project = saveTestProject("Test project", "test description");
        entry.setCategoryid(null);
        entry.setClientid(client.getClientid());
        entry.setProjectid(project.getProjectid());
        entry.setTime(4.5);
        entry.setEntryDate(new Date());
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateEntryBadRequestCategoryNotFound() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var client = saveTestClient("test client");
        final var project = saveTestProject("Test project", "test description");
        entry.setCategoryId(456);
        entry.setClientId(client.getClientid());
        entry.setProjectId(project.getProjectid());
        entry.setTimeSpent(BigDecimal.valueOf(4.5));
        entry.setDate("2021-11-10");
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    @Test
    void testCreateEntryBadRequestIdExists() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var client = saveTestClient("test client");
        final var project = saveTestProject("Test project", "test description");
        final var category = saveTestCategory("Backend");
        entry.setCategoryId(category.getCategoryid());
        entry.setClientId(client.getClientid());
        entry.setProjectId(project.getProjectid());
        entry.setTimeSpent(BigDecimal.valueOf(4.5));
        entry.setDate("2021-10-10");
        entry.setId(4);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testCreateEntryBadRequestTimeLessThatZero() throws Exception {
        //Arrange
        final var entry = new TimeSheetEntryDTO();
        final var category = saveTestCategory("test category");
        final var client = saveTestClient("test client");
        final var project = saveTestProject("Test project", "test description");
        entry.setCategoryId(category.getCategoryid());
        entry.setClientId(client.getClientid());
        entry.setProjectId(project.getProjectid());
        entry.setDate("2021-11-04");
        entry.setTimeSpent(BigDecimal.valueOf(-2.3));
        testAuthFactory.loginUser(usernameWorker);


        // Act
        final var response = mvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateEntry() throws Exception {
        //Arrange
        final var description = "5h total";
        final var entry = createTestEntry(description, registeredWorker.getTeammemberid());
        final var dto = TimeSheetEntryMapper.toEntryDTO(entry);
        final var updatedDescription = "7.5h total";
        dto.setDescription(updatedDescription);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseEntry = ResponseReader.readResponse(response, TimeSheetEntry.class);

        // Assert
        assertNotNull(responseEntry);
        assertEquals(updatedDescription, responseEntry.getDescription());
    }

    @Test
    void testUpdateSomeoneElseEntry() throws Exception {
        //Arrange
        final var description = "5h total";
        final var entry = createTestEntry(description, registeredWorker.getTeammemberid());
        final var dto = TimeSheetEntryMapper.toEntryDTO(entry);
        final var updatedDescription = "7.5h total";
        dto.setDescription(updatedDescription);
        final var newUserUsername = "jane";
        final var newUser = registerUser(newUserUsername, TeamMemberRoles.ROLE_WORKER);
        testAuthFactory.loginUser(newUserUsername);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseError = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(ErrorCode.BAD_REQUEST.toString(), responseError.getErrorCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), responseError.getStatusCode());
    }

    @Test
    void testUpdateEntryBadRequest() throws Exception {
        //Arrange
        final var description = "Work day";
        final var entry = createTestEntry(description, registeredWorker.getTeammemberid());
        final var entryDTO = TimeSheetEntryMapper.toEntryDTO(entry);
        entryDTO.setTimeSpent(BigDecimal.valueOf(25.5));
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryDTO)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateEntryBadRequestIdNotExist() throws Exception {
        //Arrange
        final var description = "Work day";
        final var entry = createTestEntry(description, registeredWorker.getTeammemberid());
        final var entryDTO = TimeSheetEntryMapper.toEntryDTO(entry);
        entryDTO.setTimeSpent(BigDecimal.valueOf(2.5));
        entryDTO.setId(null);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryDTO)))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }


    @Test
    void testUpdateEntryNotFound() throws Exception {
        //Arrange
        final var description = "Demo project";
        final var entry = createTestEntry(description, registeredWorker.getTeammemberid());
        final var entryDTO = TimeSheetEntryMapper.toEntryDTO(entry);
        entryDTO.setId(2345);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryDTO)))
                .andReturn();

        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    @Test
    void deleteEntry() throws Exception {
        //Arrange
        final var entryDescription = "FirstEntry";
        final var inserted = createTestEntry(entryDescription, registeredWorker.getTeammemberid());
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/entry/{id}", inserted.getEntryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteEntryNotFound() throws Exception {
        //Arrange
        final var entryId = 100;
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/entry/{id}", entryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var error =  ResponseReader.readResponse(response, ErrorResponse.class);

        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    private TimeSheetEntry createTestEntry(String description, int userId) {
        final var category = saveTestCategory("Test category");
        final var client = saveTestClient("Test client");
        final var project = saveTestProject("Project Name", "Project Description");
        final var entry = createTestEntry(description, category.getCategoryid(), client.getClientid(), project.getProjectid(), userId, new Date());
        return entryRepository.saveAndFlush(entry);
    }

    private Optional<TimeSheetEntry> fetchEntry(Integer id) {
        final var entry = entryRepository.findById(id);
        return entry;
    }

    private Category saveTestCategory(String categoryName) {
        final var category = createTestCategory(categoryName);
        return categoryRepository.saveAndFlush(category);
    }

    private Client saveTestClient(String clientName) {
        final var client = createTestClient(clientName);
        return clientRepository.saveAndFlush(client);
    }

    private Project saveTestProject(String projectName, String projectDescription) {
        final var project = createTestProject(projectName, projectDescription);
        return projectRepository.saveAndFlush(project);
    }

    private Teammember saveTeamMember(String name) {
        final var user = createTeamMember(name);
        return teamMemberRepository.saveAndFlush(user);
    }

    private void cleanDataBase() {
        entryRepository.deleteAll();
        entryRepository.flush();
        categoryRepository.deleteAll();
        categoryRepository.flush();
        clientRepository.deleteAll();
        clientRepository.flush();
        projectRepository.deleteAll();
        projectRepository.flush();
        teamMemberRepository.deleteAll();
        teamMemberRepository.flush();
    }
}
