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
import projekat.api.model.ProjectDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.mapper.ProjectMapper;
import projekat.models.Project;
import projekat.models.Teammember;
import projekat.repository.ProjectRepository;
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
class ProjectControllerIntegrationTest extends BaseUT{

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    private final String usernameAdmin = "adminTest";
    private final String usernameWorker = "workerTest";

    @BeforeAll
    static void setUp() {
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
    void getAllProjectsTest() throws Exception {
        //Arrange
        final var firstProjectName = "TimeSheet Application";
        final var firstProjectDescription = "Make TimeSheet Application";
        final var secondProjectName = "Booking Software";
        final var secondProjectDescription = "Make Application for Booking";
        saveTestProject(firstProjectName, firstProjectDescription);
        saveTestProject(secondProjectName, secondProjectDescription);
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(get("/project")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var projects = Arrays.asList(ResponseReader.readResponse(response, ProjectDTO[].class));

        //Assert
        assertEquals(2, projects.size());
        assertEquals(firstProjectName, projects.get(0).getName());
        assertEquals(firstProjectDescription, projects.get(0).getDescription());
        assertEquals(secondProjectName, projects.get(1).getName());
        assertEquals(secondProjectDescription, projects.get(1).getDescription());
    }

    @Test
    void getOneProjectTest() throws Exception {
        //Arrange
        final var projectName = "Booking Software";
        final var projectDescription = "Make Application for Booking";
        final var insertedProject = saveTestProject(projectName, projectDescription);
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(get("/project/{id}", insertedProject.getProjectid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var project = ResponseReader.readResponse(response, ProjectDTO.class);

        //Assert
        assertEquals(insertedProject.getProjectid(), project.getId());
        assertEquals(projectName, project.getName());
        assertEquals(projectDescription, project.getDescription());
    }

    @Test
    void getOneProjectNotFoundTest() throws Exception {
        //Arrange
        final var projectId = 100;
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(get("/project/{id}", projectId))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void testCreateProject() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "Make App for ticket reservation";
        final var project = new ProjectDTO();
        project.setName(projectName);
        project.setDescription(projectDescription);
        project.setStatus("Active");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseProject = ResponseReader.readResponse(response, ProjectDTO.class);

        // Assert
        assertNotNull(responseProject.getId());
        assertEquals(projectName, responseProject.getName());
        assertEquals(projectDescription, responseProject.getDescription());
    }

    @Test
    void testCreateProjectForbidden() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "Make App for ticket reservation";
        final var project = new ProjectDTO();
        project.setName(projectName);
        project.setDescription(projectDescription);
        project.setStatus("Active");
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseCategory.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseCategory.getErrorCode());
    }

    @Test
    void testCreateProjectBadRequest() throws Exception {
        //Arrange
        final var project = new ProjectDTO();
        project.setName("  ");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateProjectNameNotExist() throws Exception {
        //Arrange
        final var project = new ProjectDTO();
        project.setStatus("Active");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateProjectIdExists() throws Exception {
        //Arrange
        final var projectName = "Weather App";
        final var projectDescription = "Make app for weather forecast";
        final var project = new ProjectDTO();
        project.setId(5);
        project.setName(projectName);
        project.setDescription(projectDescription);
        project.setStatus("Active");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateProject() throws Exception {
        //Arrange
        final var projectName = "Project Title";
        final var projectDescription = "Project Description";
        final var insertedProject = saveTestProject(projectName, projectDescription);
        final var updatedName = "Booking App";
        final var dto = ProjectMapper.toProjectDTO(insertedProject);
        dto.setName(updatedName);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseProject = ResponseReader.readResponse(response, ProjectDTO.class);

        // Assert
        assertNotNull(responseProject.getId());
        assertEquals(updatedName, responseProject.getName());
        assertEquals(projectDescription, responseProject.getDescription());
    }

    @Test
    void testUpdateProjectForbidden() throws Exception {
        //Arrange
        final var projectName = "Project Title";
        final var projectDescription = "Project Description";
        final var insertedProject = saveTestProject(projectName, projectDescription);
        final var updatedName = "Booking App";
        final var dto = ProjectMapper.toProjectDTO(insertedProject);
        dto.setName(updatedName);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var responseProject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseProject.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseProject.getErrorCode());
    }

    @Test
    void testUpdateProjectBadRequest() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "App for ticket reservation";
        final var inserted = saveTestProject(projectName, projectDescription);
        final var updatedName = "   ";
        inserted.setProjectname(updatedName);
        final var dto = ProjectMapper.toProjectDTO(inserted);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateProjectWrongId() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "App for ticket reservation";
        final var inserted = saveTestProject(projectName, projectDescription);
        inserted.setProjectid(9999);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProjectMapper.toProjectDTO(inserted)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void testUpdateProjectIdNotPresent() throws Exception {
        //Arrange
        final var projectName = "Weather App";
        final var projectDescription = "Make app for weather forecast";
        final var project = new ProjectDTO();
        project.setName(projectName);
        project.setDescription(projectDescription);
        project.setStatus("Active");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void deleteProjectTest() throws Exception {
        //Arrange
        final var projectName = "TimeSheet App";
        final var projectDescription = "Application for time tracking";
        final var insertedProject = saveTestProject(projectName, projectDescription);
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/project/{id}", insertedProject.getProjectid()))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteProjectTestForbidden() throws Exception {
        //Arrange
        final var projectName = "TimeSheet App";
        final var projectDescription = "Application for time tracking";
        final var insertedProject = saveTestProject(projectName, projectDescription);
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(delete("/project/{id}", insertedProject.getProjectid()))
                .andReturn();

        final var errorResponse = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), errorResponse.getErrorCode());
    }

    @Test
    void deleteProjectNotFound() throws Exception {
        //Arrange
        final var projectId = 100;
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/project/{id}", projectId))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void filterProjectsTest() throws Exception {
        // Arrange
        final var firstProjectName = "App for Cinema";
        final var secondProjectName = "Time Tracking App";
        final var thirdProjectName = "App for Booking";
        final var fourthProjectName = "App for Food Ordering";
        saveTestProject(firstProjectName, null);
        saveTestProject(secondProjectName, null);
        saveTestProject(thirdProjectName, null);
        saveTestProject(fourthProjectName, null);
        final var paramName = "keyword";
        final var paramValue = "app";
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(get("/project/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredProjects = Arrays.asList(ResponseReader.readResponse(response, ProjectDTO[].class));

        // Assert
        assertEquals(3, filteredProjects.size());
        assertEquals(firstProjectName, filteredProjects.get(0).getName());
        assertEquals(thirdProjectName, filteredProjects.get(1).getName());
        assertEquals(fourthProjectName, filteredProjects.get(2).getName());
    }

    @Test
    void filterProjectsEmptyTest() throws Exception {
        // Arrange
        final var firstProjectName = "App for Cinema";
        final var secondProjectName = "Time Tracking App";
        saveTestProject(firstProjectName, null);
        saveTestProject(secondProjectName, null);
        final var paramName = "keyword";
        final var paramValue = "very long value";
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(get("/project/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredProjects = Arrays.asList(ResponseReader.readResponse(response, ProjectDTO[].class));

        // Assert
        assertEquals(0, filteredProjects.size());
    }

    private Project saveTestProject(String projectName, String projectDescription) {
        final var project = createTestProject(projectName, projectDescription);
        return repository.saveAndFlush(project);
    }


    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
        teamMemberRepository.deleteAll();
        teamMemberRepository.flush();
    }
}
