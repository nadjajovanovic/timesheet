package projekat.controller;

import java.util.Collection;
import java.util.Optional;

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

@RestController
public class TeamMemberController {
	
	private TeamMemberRepository teamMemberRepository;
	
	public TeamMemberController(TeamMemberRepository teamMemberRepository) {
		this.teamMemberRepository = teamMemberRepository;
	}
	
	@GetMapping("teammember")
	public Collection<Teammember> getTeamMembers() {
		return teamMemberRepository.findAll();
	}
	
	@GetMapping("/teammember/{teammemberid}")
	public ResponseEntity<Teammember> getTeamMember(@PathVariable Integer teammemberid) {
		Optional<Teammember> teamMember = teamMemberRepository.findById(teammemberid);
		if (!teamMember.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return  new ResponseEntity<>(teamMember.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("teammember")
	public ResponseEntity<Teammember> insertClient(@RequestBody Teammember teamMember) {
		if (teamMember.getTeammembername() == null || teamMember.getTeammembername().trim().equals("")
				|| teamMember.getTeammemberid() != null) {
			return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Teammember team = teamMemberRepository.save(teamMember);
		return new ResponseEntity<Teammember>(team, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("teammember")
	public ResponseEntity<Teammember> updateClient (@RequestBody Teammember teamMember) {
		if (!teamMemberRepository.existsById(teamMember.getTeammemberid())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (teamMember.getTeammembername() == null || teamMember.getTeammembername().trim().equals("")
				|| teamMember.getTeammemberid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Teammember inserted = teamMemberRepository.save(teamMember);
		return new ResponseEntity<>(inserted, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("teammember/{teammemberid}")
	public ResponseEntity<Teammember> deleteClient(@PathVariable Integer teammemberid) {
		if (!teamMemberRepository.existsById(teammemberid)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		teamMemberRepository.deleteById(teammemberid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
