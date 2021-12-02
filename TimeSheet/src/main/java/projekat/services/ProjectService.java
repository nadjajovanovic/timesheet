package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import projekat.exception.InputFieldException;
import projekat.exception.NotFoundException;
import projekat.models.Project;
import projekat.repository.ProjectRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Collection<Project> getAll(){
        final var projects = projectRepository.findAll();
        return projects;
    }

    public Optional<Project> getOne(Integer id){
        if (!projectRepository.existsById(id)) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var project = projectRepository.findById(id);
        return project;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Project create(Project project){
        if (project.getProjectid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }
        final var insertedProject = projectRepository.save(project);
        return insertedProject;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Project update(Project project){
        if (project.getProjectid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.BAD_REQUEST);
        }
        if(!projectRepository.existsById(project.getProjectid())) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", project.getProjectid()),HttpStatus.NOT_FOUND);
        }
        final var updatedProject = projectRepository.save(project);
        return updatedProject;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(Integer id) {
        if(!projectRepository.existsById(id)) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", id),HttpStatus.NOT_FOUND);
        }
        projectRepository.deleteById(id);
        return true;
    }

    public Collection<Project> filterByName(String keyword){
        final var allProjects = projectRepository.findAll();
        final var filteredProjects =
                    allProjects.stream()
                            .filter(e -> e.getProjectname()
                                           .toLowerCase()
                                             .startsWith(keyword.toLowerCase()))
                            .toList();
        return filteredProjects;
    }
}
