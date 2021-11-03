package projekat.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import projekat.models.Project;
import projekat.service.ProjectService;

@RestController
public class ProjectController {

	@Autowired
	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@GetMapping("project")
	public ResponseEntity<Collection<Project>> getAllProjects() {
		final var projects = projectService.getAll();
		return new ResponseEntity<>(projects, HttpStatus.OK);
	}
	
	@GetMapping("project/{projectid}")
	public ResponseEntity<Project> getProject(@PathVariable Integer projectid) {
		final var optionalProject = projectService.getOne(projectid);
		if (optionalProject.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(optionalProject.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("project")
	public ResponseEntity<Project> insertProject(@RequestBody Project project) {
		if (project.getProjectname() == null || project.getProjectname().trim().equals("")
			|| project.getProjectid() != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var insertedProject = projectService.create(project);
		return new ResponseEntity<>(insertedProject, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("project")
	public ResponseEntity<Project> updateProject(@RequestBody Project project) {
		if (project.getProjectname() == null || project.getProjectname().trim().equals("")
				|| project.getProjectid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updatedProject = projectService.update(project);
		if(updatedProject == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("project/{projectid}")
	public ResponseEntity<Project> deleteProject(@PathVariable Integer projectid) {
		final var deleted = projectService.delete(projectid);
		if(!deleted)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("project/filter")
	public ResponseEntity<Collection<Project>> filterProjectsByName(@Param("keyword") String keyword) {
		final var filteredProjects = projectService.filterByName(keyword);
		return new ResponseEntity<>(filteredProjects, HttpStatus.OK);
	}
}
