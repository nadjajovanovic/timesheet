package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import projekat.TimeSheetApplication;
import projekat.models.Client;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TeamMemberControllerIntegrationTest {

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
        //Arange
        final var firstTeamMemberName = "First";
        createTestTeamMember(firstTeamMemberName);

        //Act
        final var response = mvc.perform(get("/teammember")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teamMembers = Arrays.asList(ResponseReader.readResponse(response, Teammember[].class));

        //Assert
       assertEquals(teamMembers.size(), 1);
       assertEquals(teamMembers.get(0).getTeammembername(), firstTeamMemberName);
    }

    @Test
    public void getOneTeamMember() throws Exception {
        //Arange
        final var teamMemberNAme = "First";
        final var inserted = createTestTeamMember(teamMemberNAme);

        //Act
        final var response = mvc.perform(get("/teammember/{teammemberid}", inserted.getTeammemberid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var teammember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertEquals(teammember.getTeammembername(), teamMemberNAme);
        assertEquals(teammember.getTeammemberid(), Integer.valueOf(inserted.getTeammemberid()));
    }

    @Test
    public void getOneTeamMemberNotFound() throws Exception {
        //Arange
        final var teammemberId = "100";

        //act
        final var response = mvc.perform(get("/teammember/{teammemberid}", teammemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    /*@Test
    public void testTeamMember() throws Exception {
        //Arrange
        final var teamMemberName = "Please insert me";
        final var teammember = new Teammember();
        teammember.setTeammembername(teamMemberName);

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teammember))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseTeamMember = ResponseReader.readResponse(response, Teammember.class);

        //Assert
        assertNotNull(responseTeamMember.getTeammemberid());
        assertEquals(responseTeamMember.getTeammembername(), teamMemberName);
    }*/

    @Test
    public void testCreateTeamMemberBadRequest() throws Exception {
        //Arange
        final var teammember = new Teammember();
        teammember.setTeammembername("");

        //Act
        final var response = mvc.perform(post("/teammember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teammember))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }
    
    private Teammember createTestTeamMember(String teamMemberName) {
        final var teamMember  = new Teammember();
        teamMember.setTeammembername(teamMemberName);
        return repository.saveAndFlush(teamMember);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
