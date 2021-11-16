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
import projekat.TimeSheetApplication;
import projekat.api.model.TeamMemberDTO;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
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
class TeamMemberControllerIntegrationTest extends BaseUT{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamMemberRepository repository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void doCleanDatabase() {
        cleanDataBase();
    }

    @Test
    void getAllTeamMembers() throws Exception {
        //Arrange
        final var teamMemberName = "First";
        saveTeamMember(teamMemberName);

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
        final var teamMemberName = "First";
        final var inserted = saveTeamMember(teamMemberName);

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
        //Arange
        final var teamMemberId = "100";

        //Act
        final var response = mvc.perform(get("/teammember/{teamMemberId}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "Please insert me";
        final var teamMemberHours = 3;
        final var teamMember = new TeamMemberDTO();
        teamMember.setName(teamMemberName);
        teamMember.setUsername("username");
        teamMember.setEmail("email@gmail.com");
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
        //Arange
        final var teamMember = new Teammember();
        teamMember.setHoursperweek(2.3);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teamMember,"")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMemberNameNotExist() throws Exception {
        //Arange
        final var teamMember = new Teammember();

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teamMember,null)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateTeamMemberIdExists() throws Exception {
        //Arange
        final var teamMember = new Teammember();
        teamMember.setTeammemberid(5);
        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(teamMember,"")))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "nameForInsert";
        final var inserted = saveTeamMember(teamMemberName);
        final var updatedName = "nameForUpdate";

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO(inserted,updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertNotNull(responseTeamMember.getTeammemberid());
        assertEquals(updatedName, responseTeamMember.getTeammembername());
    }

    @Test
    void testUpdateTeamMemberBadRequest() throws Exception {
        //Arange
        final var teamMemberName = "NameForInsert";
        final var inserted = saveTeamMember(teamMemberName);
        final var updatedName = "";
        inserted.setTeammembername(updatedName);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateTeamMemberNoId() throws Exception {
        //Arange
        final var teamMember = new Teammember();
        teamMember.setTeammembername("Not important");

        //act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "Delete me";
        final var inserted = saveTeamMember(teamMemberName);

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteTeamMemberNotFound() throws Exception {
        //Arange
        final var teamMemberId = "100";

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    private Teammember saveTeamMember(String teammemberName) {
        final var teammember = new Teammember();
        teammember.setTeammembername(teammemberName);
        teammember.setHoursperweek(2.3);
        return repository.saveAndFlush(teammember);
    }

    private TeamMemberDTO saveTeamMemberDTO(Teammember t,String teammemberName) {
        final var teammember = new TeamMemberDTO();
        teammember.setId(t.getTeammemberid());
        teammember.setName(teammemberName);
        teammember.setUsername("username");
        teammember.setEmail("email@g");
        teammember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        return teammember;
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
