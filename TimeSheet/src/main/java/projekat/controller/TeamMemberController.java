package projekat.controller;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Client;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;
import projekat.services.TeamMemberService;

@RestController
public class TeamMemberController {
	
	@Autowired
	private TeamMemberService teamMemberService;
	
	public TeamMemberController(TeamMemberService teamMemberService) {
		this.teamMemberService = teamMemberService;
	}
	
	@GetMapping("teammember")
	public ResponseEntity<Collection<Teammember>> getTeamMembers() {
		final var teammembers = teamMemberService.getAll();
		return new ResponseEntity<>(teammembers, HttpStatus.OK);
	}
	
	@GetMapping("/teammember/{teammemberid}")
	public ResponseEntity<Teammember> getTeamMember(@PathVariable Integer teammemberid) {
		final var oneTeammember = teamMemberService.getOne(teammemberid);
		if (oneTeammember.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(oneTeammember.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("teammember")
	public ResponseEntity<Teammember> insertClient(@RequestBody Teammember teamMember) {
		if (teamMember.getTeammembername() == null || teamMember.getTeammembername().trim().equals("")
				|| teamMember.getTeammemberid() != null) {
			return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var inserted = teamMemberService.insert(teamMember);
		return new ResponseEntity<Teammember>(inserted, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("teammember")
	public ResponseEntity<Teammember> updateClient (@RequestBody Teammember teamMember) {
		if (teamMember.getTeammembername() == null || teamMember.getTeammembername().trim().equals("")
				|| teamMember.getTeammemberid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updated = teamMemberService.update(teamMember);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("teammember/{teammemberid}")
	public ResponseEntity<Teammember> deleteClient(@PathVariable Integer teammemberid) {
		final var deleted = teamMemberService.delete(teammemberid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
