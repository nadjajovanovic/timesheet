package projekat.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ReportControllerIntegrationTest {

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
    }

    @Test
    void getAllGeneratedReports() throws Exception {

        //Arange
        final var client = createTestClient("Nadja");
        final var category = createTestCategory("Frontend");
        final var project = createTestProject("Music App", "App for music");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        final var entry1 = createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date1);
        final var entry2 = createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date2);
        final var entry3 = createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date3);

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
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getEntryId());
        assertEquals(entry2.getEntryId(), reportResponse.get(1).getEntryId());
        assertEquals(entry3.getEntryId(), reportResponse.get(2).getEntryId());
    }

    @Test
    void generateByDateRange() throws Exception {

        //Arange
        final var client = createTestClient("Nadja");
        final var category = createTestCategory("Frontend");
        final var project = createTestProject("Music App", "App for music");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        final var entry1 = createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date1);
        createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date2);
        final var entry3 = createTestEntry(client.getClientid(), category.getCategoryid(), project.getProjectid(), date3);

        final var report = new Report();
        report.setStartdate(new GregorianCalendar(2021, Calendar.NOVEMBER, 1).getTime());
        report.setEnddate(new GregorianCalendar(2021, Calendar.NOVEMBER, 30).getTime());

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(2, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getEntryId());
        assertEquals(entry3.getEntryId(), reportResponse.get(1).getEntryId());
    }

    @Test
    void generateByClientId() throws Exception {

        //Arange
        final var client1 = createTestClient("Nadja");
        final var client2 = createTestClient("Bojana");
        final var category = createTestCategory("Frontend");
        final var project = createTestProject("Music App", "App for music");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        final var entry1 = createTestEntry(client1.getClientid(), category.getCategoryid(), project.getProjectid(), date);
        createTestEntry(client2.getClientid(), category.getCategoryid(), project.getProjectid(), date);

        final var report = new Report();
        report.setStartdate(new GregorianCalendar(2021, Calendar.NOVEMBER, 1).getTime());
        report.setEnddate(new GregorianCalendar(2021, Calendar.NOVEMBER, 30).getTime());
        report.setClientid(client1.getClientid());

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getEntryId());
    }

    @Test
    void generateByCategoryId() throws Exception {

        //Arange
        final var client = createTestClient("Nadja");
        final var category1 = createTestCategory("Frontend");
        final var category2 = createTestCategory("Backend");
        final var project = createTestProject("Music App", "App for music");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        final var entry1 = createTestEntry(client.getClientid(), category1.getCategoryid(), project.getProjectid(), date);
        createTestEntry(client.getClientid(), category2.getCategoryid(), project.getProjectid(), date);

        final var report = new Report();
        report.setCategoryid(category1.getCategoryid());

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getEntryId());
    }

    @Test
    void generateByProjectId() throws Exception {

        //Arange
        final var client = createTestClient("Nadja");
        final var category= createTestCategory("Backend");
        final var project1 = createTestProject("Cooking App", "App for cooking");
        final var project2 = createTestProject("Music App", "App for music");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        createTestEntry(client.getClientid(), category.getCategoryid(), project1.getProjectid(), date);
        final var entry2 = createTestEntry(client.getClientid(), category.getCategoryid(), project2.getProjectid(), date);

        final var report = new Report();
        report.setProjectid(project2.getProjectid());

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry2.getEntryId(), reportResponse.get(0).getEntryId());
    }

    @Test
    void testGenerateNoResults() throws Exception {

        //Arange
        final var client = createTestClient("Nadja");
        final var category1= createTestCategory("Frontend");
        final var category2= createTestCategory("Backend");
        final var project1 = createTestProject("Cooking App", "App for cooking");
        final var project2 = createTestProject("Music App", "App for music");
        final var date = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.SEPTEMBER, 5).getTime();

        createTestEntry(client.getClientid(), category1.getCategoryid(), project1.getProjectid(), date);
        createTestEntry(client.getClientid(), category1.getCategoryid(), project2.getProjectid(), date2);
        createTestEntry(client.getClientid(), category2.getCategoryid(), project2.getProjectid(), date);

        final var report = new Report();
        report.setProjectid(project2.getProjectid());
        report.setCategoryid(category1.getCategoryid());
        report.setStartdate(new GregorianCalendar(2021, Calendar.OCTOBER, 1).getTime());
        report.setEnddate(new GregorianCalendar(2021, Calendar.OCTOBER, 31).getTime());

        //Act
        final var response = mvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntry[].class));

        //assert
        assertEquals(0, reportResponse.size());
    }

    private TimeSheetEntry createTestEntry(Integer clientid, Integer categoryid, Integer projectid, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setClientid(clientid);
        entry.setCategoryid(categoryid);
        entry.setProjectid(projectid);
        entry.setEntryDate(entryDate);
        entry.setTime(2.5);
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
        categoryRepository.deleteAll();
        categoryRepository.flush();
        clientRepository.deleteAll();
        clientRepository.flush();
        projectRepository.deleteAll();
        projectRepository.flush();
    }
}
