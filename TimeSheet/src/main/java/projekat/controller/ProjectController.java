package projekat.controller;

import java.util.Collection;

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

import projekat.models.Category;
import projekat.models.Project;
import projekat.repository.ProjectRepository;

@RestController
public class ProjectController {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public ProjectController(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
	
	@GetMapping("project")
	public Collection<Project> getAllProjects() {
		return projectRepository.findAll();
	}
	
	@GetMapping("project/{projectid}")
	public Project getProject(@PathVariable Integer projectid) {
		return projectRepository.getById(projectid);
	}
	
	@CrossOrigin
	@PostMapping("project")
	public ResponseEntity<Project> insertProject(@RequestBody Project project) {
		projectRepository.save(project);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("project")
	public ResponseEntity<Project> updateProject(@RequestBody Project project) {
		if(projectRepository.existsById(project.getProjectid()))
			projectRepository.save(project);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("project/{projectid}")
	public ResponseEntity<Project> deleteProject(@PathVariable Integer projectid) {
		if(projectRepository.existsById(projectid))
			projectRepository.deleteById(projectid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
