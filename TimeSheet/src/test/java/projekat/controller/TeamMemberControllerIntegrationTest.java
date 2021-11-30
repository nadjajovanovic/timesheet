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
import projekat.api.model.TeamMemberDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
import projekat.repository.TimeSheetEntryRepository;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class TeamMemberControllerIntegrationTest extends BaseUT {

    @Autowired
    protected WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private TeamMemberRepository repository;

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

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

        registerUser(usernameAdmin, TeamMemberRoles.ROLE_ADMIN);
        registeredWorker = registerUser(usernameWorker, TeamMemberRoles.ROLE_WORKER);
    }

    @Test
    void getAllTeamMembers() throws Exception {
        //Arrange
        testAuthFactory.loginUser(usernameAdmin);

        //act
        final var response = mvc.perform(get("/teammember")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMembers = Arrays.asList(ResponseReader.readResponse(response, TeamMemberDTO[].class));

        //Assert
        assertEquals(2, teamMembers.size());
        assertEquals(usernameAdmin, teamMembers.get(0).getUsername());
        assertEquals(usernameWorker, teamMembers.get(1).getUsername());
    }

    @Test
    void getOneTeamMember() throws Exception {
        //Arrange
        final var inserted = registeredWorker;
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(get("/teammember/{id}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMember = ResponseReader.readResponse(response, TeamMemberDTO.class);

        //Assert
        assertEquals(usernameWorker, teamMember.getUsername());
        assertEquals(inserted.getTeammemberid(), teamMember.getId());
    }

    @Test
    void getOneTeamMemberNotFound() throws Exception {
        //Arrange
        final var teamMemberId = "100";
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(get("/teammember/{teamMemberId}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }

    @Test
    void testCreateTeamMember() throws Exception {
        //Arrange
        final var teamMemberName = "name";
        final var teamMemberHours = 3;
        final var teamMember = new TeamMemberDTO();
        teamMember.setName(teamMemberName);
        teamMember.setUsername("username");
        teamMember.setEmail("test@example.com");
        teamMember.setPassword("password");
        teamMember.setRepeatedPassword("password");
        teamMember.setHoursPerWeek(BigDecimal.valueOf(teamMemberHours));
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, TeamMemberDTO.class);

        //Assert
        assertNotNull(responseTeamMember.getId());
        assertEquals(teamMemberName, responseTeamMember.getName());
    }

    @Test
    void testCreateTeamMemberForbidden() throws Exception {
        //Arrange
        final var teamMemberName = "name";
        final var teamMemberHours = 3;
        final var teamMember = new TeamMemberDTO();
        teamMember.setName(teamMemberName);
        teamMember.setUsername("username");
        teamMember.setEmail("test@example.com");
        teamMember.setPassword("password");
        teamMember.setRepeatedPassword("password");
        teamMember.setHoursPerWeek(BigDecimal.valueOf(teamMemberHours));
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseError = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseError.getErrorCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), responseError.getStatusCode());
    }

    @Test
    void testCreateTeamMemberBadRequest() throws Exception {
        //Arrange
        final var teamMember = new TeamMemberDTO();
        teamMember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        teamMember.setName("");
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(HttpStatus.BAD_REQUEST.value(),response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMemberNameNotExist() throws Exception {
        //Arrange
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(registeredWorker,"")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMemberIdExists() throws Exception {
        //Arrange
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(registeredWorker,"John Doe")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateTeamMember() throws Exception {
        //Arrange
        final var updatedName = "John";
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(registeredWorker,updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, TeamMemberDTO.class);

        //Assert
        assertNotNull(responseTeamMember.getId());
        assertEquals(updatedName, responseTeamMember.getName());
    }

    @Test
    void testUpdateSomeoneElseProfile() throws Exception {
        //Arrange
        final var updatedName = "John";
        final var newUserUsername = "jane";
        registerUser(newUserUsername, TeamMemberRoles.ROLE_WORKER);
        testAuthFactory.loginUser(newUserUsername);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(registeredWorker,updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseError = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(ErrorCode.BAD_REQUEST.toString(), responseError.getErrorCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), responseError.getStatusCode());
    }

    @Test
    void testUpdateTeamMemberBadRequest() throws Exception {
        //Arrange
        final var inserted = registeredWorker;
        final var updatedName = "";
        inserted.setTeammembername(updatedName);
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(inserted,updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateTeamMemberNoId() throws Exception {
        //Arrange
        final var inserted = registeredWorker;
        inserted.setTeammemberid(null);
        testAuthFactory.loginUser(usernameAdmin);

        //act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(inserted,"name")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void deleteTeamMember() throws Exception {
        //Arrange
        final var inserted = registeredWorker;
        testAuthFactory.loginUser(usernameAdmin);

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteTeamMemberForbidden() throws Exception {
        //Arrange
        final var newUserUsername = "jane";
        final var inserted = registerUser(newUserUsername, TeamMemberRoles.ROLE_WORKER);
        testAuthFactory.loginUser(usernameWorker);

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var error =  ResponseReader.readResponse(response, ErrorResponse.class);

        //assert
        assertEquals(ErrorCode.FORBIDDEN.toString(),error.getErrorCode());
        assertEquals(HttpStatus.FORBIDDEN.value(),error.getStatusCode());
    }

    @Test
    void deleteTeamMemberNotFound() throws Exception {
        //Arrange
        final var teamMemberId = "100";
        testAuthFactory.loginUser(usernameAdmin);

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error =  ResponseReader.readResponse(response, ErrorResponse.class);

        //assert
        assertEquals(ErrorCode.NOT_FOUND.toString(),error.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(),error.getStatusCode());
    }


    private TeamMemberDTO saveTeamMemberDTO(Teammember t,String teammemberName) {
        final var teammember = new TeamMemberDTO();
        teammember.setId(t.getTeammemberid());
        teammember.setName(teammemberName);
        teammember.setUsername(t.getUsername());
        teammember.setEmail("test@example.com");
        teammember.setPassword(t.getPassword());
        teammember.setStatus(t.getStatus());
        teammember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        teammember.setRepeatedPassword(t.getPassword());
        teammember.setStatus(true);
        return teammember;
    }

    private void cleanDataBase() {
        timeSheetEntryRepository.deleteAll();
        timeSheetEntryRepository.flush();
        repository.deleteAll();
        repository.flush();
    }
}
