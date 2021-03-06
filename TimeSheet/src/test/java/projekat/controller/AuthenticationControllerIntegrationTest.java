package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import projekat.api.model.AuthenticationRequestDTO;
import projekat.api.model.AuthenticationResponseDTO;
import projekat.api.model.ResetPasswordDTO;
import projekat.api.model.TeamMemberDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class AuthenticationControllerIntegrationTest extends BaseUT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamMemberRepository repository;

    @Autowired
    protected WebApplicationContext context;

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
    void login() throws Exception {
        //Arrange
        final var username = "adminTest";
        final var password = "adminju";
        final var authenticationRequestDTO = new AuthenticationRequestDTO();
        authenticationRequestDTO.setUsername(username);
        authenticationRequestDTO.setPassword(password);

        //act
        final var response = mvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var jwt = ResponseReader.readResponse(response, AuthenticationResponseDTO.class);

        //Assert
        assertFalse( jwt.getJwt().isEmpty());
    }

    @Test
    void loginBadRequest() throws Exception {
        //Arrange
        final var username = "adminTest";
        final var password = "badPassword";
        final var authenticationRequestDTO = new AuthenticationRequestDTO();
        authenticationRequestDTO.setUsername(username);
        authenticationRequestDTO.setPassword(password);

        //act
        final var response = mvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(ErrorCode.BAD_REQUEST.toString(),error.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(),error.getStatusCode());
    }

    @Test
    void resetPasswordBadRequest() throws Exception {
        //Arrange
        final var newPassword = "admin";
        final var oldPassword = "admin";
        final var repeatedPassword = "admirtn";
        final var resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setNewPassword(newPassword);
        resetPasswordDTO.setOldPassword(oldPassword);
        resetPasswordDTO.setNewPasswordRepeated(repeatedPassword);

        //act
        final var response = mvc.perform(post("/authenticate/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(ErrorCode.BAD_REQUEST.toString(),error.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(),error.getStatusCode());
    }

    @Test
    void resetPassword() throws Exception {
        //Arrange
        final var newPassword = "admin";
        final var oldPassword = "adminju";
        final var repeatedPassword = "admin";
        final var resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setNewPassword(newPassword);
        resetPasswordDTO.setOldPassword(oldPassword);
        resetPasswordDTO.setNewPasswordRepeated(repeatedPassword);

        //act
        final var response = mvc.perform(post("/authenticate/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());

    }

    @Test
    void registration() throws Exception {

        //act
        final var response = mvc.perform(post("/authenticate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTeamMemberDTO()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());

    }

    @Test
    void registrationBadRequest() throws Exception {
        //Arrange
        final var inserted = saveTeamMemberDTO();
        inserted.setRepeatedPassword("bad");

        //act
        final var response = mvc.perform(post("/authenticate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());

    }

    protected TeamMemberDTO saveTeamMemberDTO() {
        final var teammember = new TeamMemberDTO();
        teammember.setName("name");
        teammember.setPassword("password");
        teammember.setRepeatedPassword("password");
        teammember.setUsername("admin");
        teammember.setEmail("test@example.com");
        teammember.setStatus(true);
        teammember.setHoursPerWeek(BigDecimal.valueOf(2.3));
        return teammember;
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}
