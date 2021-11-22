package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.AuthenticateApi;
import projekat.api.model.AuthenticationRequestDTO;
import projekat.api.model.AuthenticationResponseDTO;
import projekat.api.model.ResetPasswordDTO;
import projekat.api.model.TeamMemberDTO;
import projekat.exception.BadRequestException;
import projekat.exception.NotFoundException;
import projekat.mapper.TeamMemberMapper;
import projekat.services.JwtUtilService;
import projekat.services.TeamMemberService;

@RestController
public class AuthenticationController implements AuthenticateApi {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final TeamMemberService teamMemberService;

    @Autowired
    private final JwtUtilService jwtTokenUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, TeamMemberService teamMemberService, JwtUtilService jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.teamMemberService = teamMemberService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(AuthenticationRequestDTO authenticationRequestDTO) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword())
            );
        } catch (BadCredentialsException | NotFoundException ex) {
            throw new BadRequestException("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

        final var userDetails = teamMemberService.loadUserByUsername(authenticationRequestDTO.getUsername());
        final var jwt = jwtTokenUtil.generateToken(userDetails);
        final var response = new AuthenticationResponseDTO();
        response.setJwt(jwt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getNewPasswordRepeated())){
            throw new BadRequestException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        // TODO reset password

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> registerUser(TeamMemberDTO teamMemberDTO) {
        if (!teamMemberDTO.getPassword().equals(teamMemberDTO.getRepeatedPassword())){
            throw new BadRequestException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        final var teamMember = TeamMemberMapper.toTeamMember(teamMemberDTO);
        teamMemberService.insert(teamMember);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
