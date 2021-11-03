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
import projekat.models.Project;
import projekat.repository.ProjectRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectRepository repository;

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
    public void getAllProjectsTest() throws Exception {
        //Arrange
        final var firstProjectName = "TimeSheet Application";
        final var firstProjectDescription = "Make TimeSheet Application";
        final var secondProjectName = "Booking Software";
        final var secondProjectDescription = "Make Application for Booking";
        createTestProject(firstProjectName, firstProjectDescription);
        createTestProject(secondProjectName, secondProjectDescription);

        //Act
        final var response = mvc.perform(get("/project")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var projects = Arrays.asList(ResponseReader.readResponse(response, Project[].class));

        //Assert
        assertEquals(projects.size(), 2);
        assertEquals(projects.get(0).getProjectname(), firstProjectName);
        assertEquals(projects.get(0).getProjectdescription(), firstProjectDescription);
        assertEquals(projects.get(1).getProjectname(), secondProjectName);
        assertEquals(projects.get(1).getProjectdescription(), secondProjectDescription);
    }

    @Test
    public void getOneProjectTest() throws Exception {
        //Arrange
        final var projectName = "Booking Software";
        final var projectDescription = "Make Application for Booking";
        final var insertedProject = createTestProject(projectName, projectDescription);

        //Act
        final var response = mvc.perform(get("/project/{id}", insertedProject.getProjectid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var project = ResponseReader.readResponse(response, Project.class);

        //Assert
        assertEquals(project.getProjectid(), insertedProject.getProjectid());
        assertEquals(project.getProjectname(), projectName);
        assertEquals(project.getProjectdescription(), projectDescription);
    }

    @Test
    public void  getOneProjectNotFoundTest() throws Exception {
        //Arrange
        final var projectId = 100;

        //Act
        final var response = mvc.perform(get("/project/{id}", projectId))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void  testCreateProject() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "Make App for ticket reservation";
        final var project = new Project();
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseProject = ResponseReader.readResponse(response, Project.class);

        // Assert
        assertNotNull(responseProject.getProjectid());
        assertEquals(responseProject.getProjectname(), projectName);
        assertEquals(responseProject.getProjectdescription(), projectDescription);
    }

    @Test
    public void  testCreateProjectBadRequest() throws Exception {
        //Arrange
        final var project = new Project();
        project.setProjectname("  ");

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateProjectNameNotExist() throws Exception {
        //Arrange
        final var project = new Project();

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateProjectIdExists() throws Exception {
        //Arrange
        final var projectName = "Weather App";
        final var projectDescription = "Make app for weather forecast";
        final var project = new Project();
        project.setProjectid(5);
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);

        // Act
        final var response = mvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testUpdateProject() throws Exception {
        //Arrange
        final var projectName = "Project Title";
        final var projectDescription = "Project Description";
        final var insertedProject = createTestProject(projectName, projectDescription);
        final var updatedName = "Booking App";
        insertedProject.setProjectname(updatedName);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedProject))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseProject = ResponseReader.readResponse(response, Project.class);

        // Assert
        assertNotNull(responseProject.getProjectid());
        assertEquals(responseProject.getProjectname(), updatedName);
        assertEquals(responseProject.getProjectdescription(), projectDescription);
    }

    @Test
    public void  testUpdateProjectBadRequest() throws Exception {
        //Arrange
        final var projectName = "Cinema App";
        final var projectDescription = "App for ticket reservation";
        final var inserted = createTestProject(projectName, projectDescription);
        final var updatedName = "   ";
        inserted.setProjectname(updatedName);

        // Act
        final var response = mvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted)))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteCProjectTest() throws Exception {
        //Arrange
        final var projectName = "TimeSheet App";
        final var projectDescription = "Application for time tracking";
        final var insertedProject = createTestProject(projectName, projectDescription);

        //Act
        final var response = mvc.perform(delete("/project/{id}", insertedProject.getProjectid()))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteProjectNotFound() throws Exception {
        //Arrange
        final var projectId = 100;

        //Act
        final var response = mvc.perform(delete("/project/{id}", projectId))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void filterProjectsTest() throws Exception {
        // Arrange
        final var firstProjectName = "App for Cinema";
        final var secondProjectName = "Time Tracking App";
        final var thirdProjectName = "App for Booking";
        final var fourthProjectName = "App for Food Ordering";
        createTestProject(firstProjectName, null);
        createTestProject(secondProjectName, null);
        createTestProject(thirdProjectName, null);
        createTestProject(fourthProjectName, null);
        final var paramName = "keyword";
        final var paramValue = "app";

        // Act
        final var response = mvc.perform(get("/project/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredProjects = Arrays.asList(ResponseReader.readResponse(response, Project[].class));

        // Assert
        assertEquals(filteredProjects.size(), 3);
        assertEquals(filteredProjects.get(0).getProjectname(), firstProjectName);
        assertEquals(filteredProjects.get(1).getProjectname(), thirdProjectName);
        assertEquals(filteredProjects.get(2).getProjectname(), fourthProjectName);
    }

    @Test
    public void filterProjectsEmptyTest() throws Exception {
        // Arrange
        final var firstProjectName = "App for Cinema";
        final var secondProjectName = "Time Tracking App";
        createTestProject(firstProjectName, null);
        createTestProject(secondProjectName, null);
        final var paramName = "keyword";
        final var paramValue = "very long value";

        // Act
        final var response = mvc.perform(get("/project/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andReturn();
        final var filteredProjects = Arrays.asList(ResponseReader.readResponse(response, Project[].class));

        // Assert
        assertEquals(filteredProjects.size(), 0);
    }

    private Project createTestProject(String projectName, String projectDescription) {
        final var project = new Project();
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);
        return repository.saveAndFlush(project);
    }


    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
