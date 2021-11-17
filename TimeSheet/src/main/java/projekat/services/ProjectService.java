package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
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
            throw new NotFoundException(String.format("Object with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
        }
        final var project = projectRepository.findById(id);
        return project;
    }

    public Project create(Project project){
        final var insertedProject = projectRepository.save(project);
        return insertedProject;
    }

    public Project update(Project project){
        if(!projectRepository.existsById(project.getProjectid())) {
            throw new NotFoundException(String.format("Object with id %d does not exist in database", project.getProjectid()), ErrorCode.NOT_FOUND);
        }
        final var updatedProject = projectRepository.save(project);
        return updatedProject;
    }

    public boolean delete(Integer id) {
        if(!projectRepository.existsById(id)) {
            throw new NotFoundException(String.format("Object with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
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
