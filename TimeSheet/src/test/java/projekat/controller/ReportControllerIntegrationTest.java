package projekat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import projekat.TimeSheetApplication;
import projekat.models.*;
import projekat.repository.*;
import projekat.util.ResponseReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Time;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void settingUpDatabase() {
        cleanDataBase();
        final var client1 = createTestClient("Nadja");
        final var client2 = createTestClient("Bojana");
        final var client3 = createTestClient("Dusan");

        final var category1 = createTestCategory("Frontend");
        final var category2 = createTestCategory("Backend");
        final var category3 = createTestCategory("QA Testing");

        final var project1 = createTestProject("Music App", "App for music");
        final var project2 = createTestProject("Cooking App", "App for cooking");
        final var project3 = createTestProject("Book App", "App for books");

        final var entry1 = createTestEntry(client2.getClientid(), category1.getCategoryid(), project3.getProjectid(), new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime());
        final var entry2 = createTestEntry(client1.getClientid(), category3.getCategoryid(), project1.getProjectid(), new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime());
        final var entry3 = createTestEntry(client3.getClientid(), category2.getCategoryid(), project2.getProjectid(), new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime());
    }

    /*@Test
    void getAllGeneratedReports() throws Exception {
        //Arange
        final var report = new Report();

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(3, reportResponse.size());

    }*/

    private TimeSheetEntry createTestEntry(Integer clientid, Integer categoryid, Integer projectid, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setClientid(clientid);
        entry.setCategoryid(categoryid);
        entry.setProjectid(projectid);
        entry.setEntryDate(entryDate);
        return timeSheetEntryRepository.saveAndFlush(entry);
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
        timeSheetEntryRepository.deleteAll();
        timeSheetEntryRepository.flush();
    }
}
