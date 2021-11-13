package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.ProjectApi;
import projekat.api.model.ProjectDTO;
import projekat.mapper.ProjectMapper;
import projekat.services.ProjectService;

import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

	@Autowired
	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Override
	public ResponseEntity<List<ProjectDTO>> getAllProjects() {
		final var projects = projectService.getAll();
		final var dtos = projects
				.stream()
				.map(ProjectMapper::toProjectDTO)
				.toList();
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ProjectDTO> getProject(@PathVariable Integer projectid) {
		final var optionalProject = projectService.getOne(projectid);
		if (optionalProject.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		final var projectDTO = ProjectMapper.toProjectDTO(optionalProject.get());
		return new ResponseEntity<>(projectDTO, HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<ProjectDTO> insertProject(@RequestBody ProjectDTO projectDTO) {
		if (projectDTO.getName() == null || projectDTO.getName().trim().equals("")
			|| projectDTO.getId() != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var project = ProjectMapper.fromProjectDTO(projectDTO);
		final var insertedProject = projectService.create(project);
		final var insertedProjectDTO = ProjectMapper.toProjectDTO(insertedProject);
		return new ResponseEntity<>(insertedProjectDTO, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<ProjectDTO> updateProject(@RequestBody ProjectDTO projectDTO) {
		final var project = ProjectMapper.fromProjectDTO(projectDTO);
		final var updatedProject = projectService.update(project);
		if(updatedProject == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		final var updatedDTO = ProjectMapper.toProjectDTO(updatedProject);
		return new ResponseEntity<>(updatedDTO, HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Integer projectid) {
		final var deleted = projectService.delete(projectid);
		if(!deleted)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<ProjectDTO>> filterProjectsByName(@Param("keyword") String keyword) {
		final var filteredProjects = projectService.filterByName(keyword);
		final var dtos = filteredProjects
				.stream()
				.map(ProjectMapper::toProjectDTO)
				.toList();
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
}
