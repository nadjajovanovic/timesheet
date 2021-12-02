package projekat.controller;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.*;
import projekat.api.api.TeammemberApi;
import projekat.api.model.TeamMemberDTO;
import projekat.exception.NotFoundException;
import projekat.mapper.TeamMemberMapper;
import projekat.models.Teammember;
import projekat.services.JwtUtilService;
import projekat.services.TeamMemberService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class TeamMemberController implements TeammemberApi {

	@Autowired
	private final TeamMemberService teamMemberService;

	@Autowired
	private final JwtUtilService jwtUtil;

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	private Configuration config;

	public TeamMemberController(TeamMemberService teamMemberService, JwtUtilService jwtUtil) {
		this.teamMemberService = teamMemberService;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public ResponseEntity<List<TeamMemberDTO>> getTeamMembers() {
		final var teammembers = teamMemberService.getAll()
				.stream()
				.map(TeamMemberMapper::toTeamMemberDTO)
				.toList();
		return new ResponseEntity(teammembers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TeamMemberDTO> getTeamMember(@PathVariable Integer teammemberid) {
		final var oneTeammember = teamMemberService.getOne(teammemberid);
		return new ResponseEntity(TeamMemberMapper.toTeamMemberDTO(oneTeammember.get()), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> insertTeamMember(@RequestBody TeamMemberDTO teamMember) {
		final var inserted = teamMemberService.insert(TeamMemberMapper.toTeamMember(teamMember));
		return new ResponseEntity(TeamMemberMapper.toTeamMemberDTO(inserted), HttpStatus.CREATED);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> updateTeamMember (@RequestBody TeamMemberDTO teamMember) {
		final var updated = teamMemberService.update(TeamMemberMapper.toTeamMember(teamMember));
		return new ResponseEntity(TeamMemberMapper.toTeamMemberDTO(updated), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> deleteTeamMember(@PathVariable Integer teammemberid) {
		teamMemberService.delete(teammemberid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
