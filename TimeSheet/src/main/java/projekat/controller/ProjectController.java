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
	public ResponseEntity<Project> getProject(@PathVariable Integer projectid) {
		final var optionalProject = projectRepository.findById(projectid);
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
		final var insertedProject = projectRepository.save(project);
		return new ResponseEntity<>(insertedProject, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("project")
	public ResponseEntity<Project> updateProject(@RequestBody Project project) {
		if (project.getProjectname() == null || project.getProjectname().trim().equals("")
				|| project.getProjectid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(!projectRepository.existsById(project.getProjectid()))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		final var updatedProject = projectRepository.save(project);
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("project/{projectid}")
	public ResponseEntity<Project> deleteProject(@PathVariable Integer projectid) {
		if(!projectRepository.existsById(projectid))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		projectRepository.deleteById(projectid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("project/filter")
	public ResponseEntity<Collection<Project>> filterProjectsByName(@Param("keyword") String keyword) {
		return new ResponseEntity<>(projectRepository.findByProjectnameStartingWithIgnoreCase(keyword)
										, HttpStatus.OK);
	}
}
