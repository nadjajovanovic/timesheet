package projekat.controller;

import java.util.Collection;

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
	public Teammember getTeamMember(@PathVariable Integer teammemberid) {
		return teamMemberRepository.getById(teammemberid);
	}
	
	@CrossOrigin
	@PostMapping("teammember")
	public ResponseEntity<Teammember> insertClient(@RequestBody Teammember teamMember) {
		teamMemberRepository.save(teamMember);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("teammember")
	public ResponseEntity<Teammember> updateClient (@RequestBody Teammember teamMember) {
		if (teamMemberRepository.existsById(teamMember.getTeammemberid()))
			teamMemberRepository.save(teamMember);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("teammember/{teammemberid}")
	public ResponseEntity<Teammember> deleteClient(@PathVariable Integer teammemberid) {
		if (teamMemberRepository.existsById(teammemberid))
			teamMemberRepository.deleteById(teammemberid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
