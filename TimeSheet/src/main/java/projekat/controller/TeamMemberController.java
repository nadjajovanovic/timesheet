package projekat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.TeammemberApi;
import projekat.api.model.TeamMemberDTO;
import projekat.mapper.TeamMemberMapper;
import projekat.services.JwtUtilService;
import projekat.services.TeamMemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TeamMemberController implements TeammemberApi {

	@Autowired
	private final TeamMemberService teamMemberService;

	@Autowired
	private final JwtUtilService jwtUtil;

	public TeamMemberController(TeamMemberService teamMemberService, JwtUtilService jwtUtil) {
		this.teamMemberService = teamMemberService;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public ResponseEntity<List<TeamMemberDTO>> getTeamMembers() {
		final var teammembers = teamMemberService.getAll()
				.stream()
				.map(TeamMemberMapper::toTeamMemberDTO)
				.collect(Collectors.toList());
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
		return new ResponseEntity(updated, HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> deleteTeamMember(@PathVariable Integer teammemberid) {
		teamMemberService.delete(teammemberid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
