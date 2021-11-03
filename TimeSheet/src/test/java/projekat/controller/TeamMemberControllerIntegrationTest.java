package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.MD5Digest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import projekat.TimeSheetApplication;
import projekat.models.Client;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MvcResult;

import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class TeamMemberControllerIntegrationTest {

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
    public void doCleanDatabase() {
        cleanDataBase();
    }

    @Test
    public void getAllTeamMembers() throws Exception {
        //Arrange
        final var teamMemberName = "First";
        createTeamMember(teamMemberName);

        //act
        final var response = mvc.perform(get("/teammember")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMembers = Arrays.asList(ResponseReader.readResponse(response, Teammember[].class));

        //Assert
        assertEquals(teamMembers.size(), 1);
        assertEquals(teamMembers.get(0).getTeammembername(), teamMemberName);
    }

    //@org.junit.Test
    @Test
    public void getOneTeamMember() throws Exception {
        //Arrange
        final var teamMemberName = "First";
        final var inserted = createTeamMember(teamMemberName);

        //Act
        final var response = mvc.perform(get("/teammember/{id}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertEquals(teamMember.getTeammembername(), teamMemberName);
        assertEquals(teamMember.getTeammemberid(), Integer.valueOf(inserted.getTeammemberid()));
    }

    @Test
    public void getOneTeamMemberNotFound() throws Exception {
        //Arange
        final var teamMemberId = "100";

        //Act
        final var response = mvc.perform(get("/teammember/{teamMemberId}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCreateTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "Please insert me";
        final var teamMember = new Teammember();
        teamMember.setTeammembername(teamMemberName);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertNotNull(responseTeamMember.getTeammemberid());
        assertEquals(responseTeamMember.getTeammembername(), teamMemberName);
    }

    @Test
    public void testCreateTeamMemberBadRequest() throws Exception {
        //Arange
        final var teamMember = new Teammember();
        teamMember.setTeammembername("");

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testCreateTeamMemberNameNotExist() throws Exception {
        //Arange
        final var teamMember = new Teammember();

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testCreateTeamMemberIdExists() throws Exception {
        //Arange
        final var teamMemberName = "Please insert me";
        final var teamMember = new Teammember();
        teamMember.setTeammembername(teamMemberName);
        teamMember.setTeammemberid(5);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "nameForInsert";
        final var inserted = createTeamMember(teamMemberName);
        final var updatedName = "nameForUpdate";
        inserted.setTeammembername(updatedName);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertNotNull(responseTeamMember.getTeammemberid());
        assertEquals(responseTeamMember.getTeammembername(), updatedName);
    }

    @Test
    public void testUpdateTeamMemberBadRequest() throws Exception {
        //Arange
        final var teamMemberName = "NameForInsert";
        final var inserted = createTeamMember(teamMemberName);
        final var updatedName = "";
        inserted.setTeammembername(updatedName);

        //Act
        final var response = mvc.perform(put("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test @Disabled
    public void testUpdateTeamMemberNoId() throws Exception {
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
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteTeamMember() throws Exception {
        //Arange
        final var teamMemberName = "Delete me";
        final var inserted = createTeamMember(teamMemberName);

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteTeamMemberNotFound() throws Exception {
        //Arange
        final var teamMemberId = "100";

        //act
        final var response = mvc.perform(delete("/teammember/{teammemberid}", teamMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    private Teammember createTeamMember(String teammemberName) {
        final var teammember = new Teammember();
        teammember.setTeammembername(teammemberName);
        return repository.saveAndFlush(teammember);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
