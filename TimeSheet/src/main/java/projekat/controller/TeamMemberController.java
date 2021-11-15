package projekat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.TeammemberApi;
import projekat.api.model.TeamMemberDTO;
import projekat.mapper.TeamMemberMapper;
import projekat.services.TeamMemberService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TeamMemberController implements TeammemberApi {

	@Autowired
	private TeamMemberService teamMemberService;

	public TeamMemberController(TeamMemberService teamMemberService) {
		this.teamMemberService = teamMemberService;
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
		if (oneTeammember.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(TeamMemberMapper.toTeamMemberDTO(oneTeammember.get()), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> insertTeamMember(@RequestBody TeamMemberDTO teamMember) {
		final var inserted = teamMemberService.insert(TeamMemberMapper.toTeamMember(teamMember));
		if(inserted.getTeammembername() == null || inserted.getTeammembername().trim().equals(""))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity(TeamMemberMapper.toTeamMemberDTO(inserted), HttpStatus.CREATED);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> updateTeamMember (@RequestBody TeamMemberDTO teamMember) {
		final var updated = teamMemberService.update(TeamMemberMapper.toTeamMember(teamMember));
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(updated, HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<TeamMemberDTO> deleteTeamMember(@PathVariable Integer teammemberid) {
		final var deleted = teamMemberService.delete(teammemberid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
