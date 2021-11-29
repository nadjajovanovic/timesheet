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

import static org.junit.jupiter.api.Assertions.*;
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

    private Teammember teammember;


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

        final var username = "adminTest";
        teammember = registerUser(username, TeamMemberRoles.ROLE_ADMIN);
        testAuthFactory.loginUser(username);
    }

    @Test
    void getAllTeamMembers() throws Exception {
        //Arrange
        final var teamMemberName = "name";

        //act
        final var response = mvc.perform(get("/teammember")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMembers = Arrays.asList(ResponseReader.readResponse(response, TeamMemberDTO[].class));

        //Assert
        assertEquals(1, teamMembers.size());
        assertEquals(teamMemberName, teamMembers.get(0).getName());
    }

    @Test
    void getOneTeamMember() throws Exception {
        //Arrange
        final var teamMemberName = "name";
        final var inserted = teammember;

        //Act
        final var response = mvc.perform(get("/teammember/{id}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMember = ResponseReader.readResponse(response, TeamMemberDTO.class);

        //Assert
        assertEquals(teamMemberName, teamMember.getName());
        assertEquals(inserted.getTeammemberid(), teamMember.getId());
    }

    @Test
    void getOneTeamMemberNotFound() throws Exception {
        //Arrange
        final var teamMemberId = "100";
//        saveTeamMember("jhon");
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
    void testCreateTeamMemberBadRequest() throws Exception {
        //Arrange
        final var teamMember = new TeamMemberDTO();
        teamMember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        teamMember.setName("");
        //saveTeamMember("jhon");
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
        //final var teamMember = saveTeamMember("jhon");
        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teammember,"")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMemberIdExists() throws Exception {
        //Arrange
        //final var teamMember = saveTeamMember("jhon");

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teammember,"John Doe")))
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
        final var teamMemberName = "nameForInsert";
        final var updatedName = "nameForUpdate";

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teammember,updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, TeamMemberDTO.class);

        //Assert
        assertNotNull(responseTeamMember.getId());
        assertEquals(updatedName, responseTeamMember.getName());
    }

    @Test
    void testUpdateTeamMemberBadRequest() throws Exception {
        //Arrange
        final var inserted = teammember;
        final var updatedName = "";
        inserted.setTeammembername(updatedName);
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
        final var inserted = teammember;
        inserted.setTeammemberid(null);

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
        final var teamMemberName = "Delete me";
        final var inserted = teammember;

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteTeamMemberNotFound() throws Exception {
        //Arrange
        final var teamMemberId = "100";
        //saveTeamMember("jhon");
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
