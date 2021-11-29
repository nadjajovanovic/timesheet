package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import projekat.TimeSheetApplication;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.enums.TeamMemberRoles;
import projekat.models.*;
import projekat.repository.*;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ReportControllerIntegrationTest extends BaseUT{

    @Autowired
    protected WebApplicationContext context;

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

    @Autowired
    private Cache cache;

    private static ObjectMapper objectMapper;

    private Teammember teammember;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void settingUpDatabase() {
        cleanDataBase();
        cache.clear();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        teammember = registerUser("adminTest", TeamMemberRoles.ROLE_ADMIN);

        testAuthFactory.loginUser("adminTest");

    }

    @Test
    void getAllGeneratedReports() throws Exception {

        //Arange
        final var client = saveTestClient("Nadja");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        final var entry1 = saveTestEntry(client, category, project, teamMember, date1);
        final var entry2 = saveTestEntry(client, category, project, teamMember,date2);
        final var entry3 = saveTestEntry(client, category, project, teamMember,date3);

        final var report = new ReportFilterDTO();

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(3, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getId());
        assertEquals(entry2.getEntryId(), reportResponse.get(1).getId());
        assertEquals(entry3.getEntryId(), reportResponse.get(2).getId());
    }

    @Test
    void generateByDateRange() throws Exception {

        //Arange
        final var client = saveTestClient("Nadja");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        final var entry1 = saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        final var entry3 = saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setStartDate("2021-11-08");
        report.setEndDate("2021-11-30");

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(2, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getId());
        assertEquals(entry3.getEntryId(), reportResponse.get(1).getId());
    }

    @Test
    void generateByClientId() throws Exception {

        //Arange
        final var client1 = saveTestClient("Nadja");
        final var client2 = saveTestClient("Bojana");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        final var entry1 = saveTestEntry(client1, category, project, teamMember, date);
        saveTestEntry(client2, category, project, teamMember, date);

        final var report = new ReportFilterDTO();
        report.setStartDate("2021-11-1");
        report.setEndDate("2021-11-30");
        report.setClientId(client1.getClientid());

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getId());
    }

    @Test
    void generateByCategoryId() throws Exception {

        //Arange
        final var client = saveTestClient("Nadja");
        final var category1 = saveTestCategory("Frontend");
        final var category2 = saveTestCategory("Backend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        final var entry1 = saveTestEntry(client, category1, project, teamMember, date);
        saveTestEntry(client, category2, project, teamMember, date);

        final var report = new ReportFilterDTO();
        report.setCategoryId(category1.getCategoryid());

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry1.getEntryId(), reportResponse.get(0).getId());
    }

    @Test
    void generateByProjectId() throws Exception {

        //Arange
        final var client = saveTestClient("Nadja");
        final var category= saveTestCategory("Backend");
        final var project1 = saveTestProject("Cooking App", "App for cooking");
        final var project2 = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date = new GregorianCalendar(2021, Calendar.NOVEMBER, 15).getTime();

        saveTestEntry(client, category, project1, teamMember, date);
        final var entry2 = saveTestEntry(client, category, project2, teamMember, date);

        final var report = new ReportFilterDTO();
        report.setProjectId(project2.getProjectid());

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(1, reportResponse.size());
        assertEquals(entry2.getEntryId(), reportResponse.get(0).getId());
    }

    @Test
    void testGenerateNoResults() throws Exception {

        //Arange
        final var client = saveTestClient("Nadja");
        final var category1= saveTestCategory("Frontend");
        final var category2 = saveTestCategory("Backend");
        final var project1 = saveTestProject("Cooking App", "App for cooking");
        final var project2 = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.SEPTEMBER, 5).getTime();

        saveTestEntry(client, category1, project1, teamMember, date);
        saveTestEntry(client, category1, project2, teamMember, date2);
        saveTestEntry(client, category2, project2, teamMember, date);

        final var report = new ReportFilterDTO();
        report.setProjectId(project2.getProjectid());
        report.setCategoryId(category1.getCategoryid());
        report.setStartDate("2021-10-01");
        report.setEndDate("2021-10-31");

        //Act
        final var response = mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var reportResponse = Arrays.asList(ResponseReader.readResponse(response, TimeSheetEntryReportDTO[].class));

        //assert
        assertEquals(0, reportResponse.size());
    }

    @Test
    void generatePdfNoEntities() throws Exception {
        //Arange
        final var report = new ReportFilterDTO();

        //Act
        final var response = mvc.perform(get("/report/getPdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals("attachment; filename=report.pdf", response.getResponse().getHeader("Content-Disposition"));
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
    }


    @Test
    void generatePdfNoResults() throws Exception {
        //Arange
        final var client = saveTestClient("Jhon");
        final var category = saveTestCategory("Backend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setCategoryId(12);

        //Act
        final var response = mvc.perform(get("/report/getPdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=report.pdf", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generatePdfByCategoryId() throws Exception {
        //Arange
        final var client = saveTestClient("Jane");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setCategoryId(category.getCategoryid());

        //Act
        final var response = mvc.perform(get("/report/getPdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=report.pdf", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generateExcelByDateRange() throws Exception {
        //Arange
        final var client = saveTestClient("Jane");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setStartDate("2021-11-08");
        report.setEndDate("2021-11-30");

        //Act
        final var response = mvc.perform(post("/report/export/excel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=report.xlsx", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generateExcelNoEntities() throws Exception {
        //Arange
        final var report = new ReportFilterDTO();

        //Act
        final var response = mvc.perform(post("/report/export/excel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=report.xlsx", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generateExcelNoResults() throws Exception {
        //Arange
        final var client = saveTestClient("Jane");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setCategoryId(1234);

        //Act
        final var response = mvc.perform(post("/report/export/excel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report))
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=report.xlsx", response.getResponse().getHeader("Content-Disposition"));
    }


    @Test
    void generateCsvFile() throws Exception {
        //Arrange
        final var client = saveTestClient("Jane");
        final var category = saveTestCategory("Backend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setStartDate("2021-11-08");
        report.setEndDate("2021-11-30");

        //act
        final var response = mvc.perform(post("/report/export/csv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(),response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=reports.csv", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generateCsvNoEntities() throws Exception {
        //arange
        final var report = new ReportFilterDTO();

        //act
        final var response = mvc.perform(post("/report/export/csv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=reports.csv", response.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    void generateCsvNoResults() throws Exception {
        //Arange
        final var client = saveTestClient("Jane");
        final var category = saveTestCategory("Frontend");
        final var project = saveTestProject("Music App", "App for music");
        final var teamMember = saveTeamMember("John Doe");
        final var date1 = new GregorianCalendar(2021, Calendar.NOVEMBER, 11).getTime();
        final var date2 = new GregorianCalendar(2021, Calendar.OCTOBER, 15).getTime();
        final var date3 = new GregorianCalendar(2021, Calendar.NOVEMBER, 28).getTime();

        saveTestEntry(client, category, project, teamMember, date1);
        saveTestEntry(client, category, project, teamMember, date2);
        saveTestEntry(client, category, project, teamMember, date3);

        final var report = new ReportFilterDTO();
        report.setCategoryId(1234);

        //act
        final var response = mvc.perform(post("/report/export/csv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andReturn();

        //assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertNotNull(response.getResponse().getContentAsByteArray());
        assertEquals("attachment; filename=reports.csv", response.getResponse().getHeader("Content-Disposition"));
    }

    private TimeSheetEntry saveTestEntry(Client client, Category category, Project project, Teammember teammember, Date entryDate) {
        final var entry = createTestEntryWithObjects("Description", category, client, project, teammember, entryDate);
        return timeSheetEntryRepository.saveAndFlush(entry);
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
        timeSheetEntryRepository.deleteAll();
        timeSheetEntryRepository.flush();
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
