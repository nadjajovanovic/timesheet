package projekat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        final var project = projectRepository.findById(id);
        return project;
    }

    public Project create(Project project){
        final var insertedProject = projectRepository.save(project);
        return insertedProject;
    }

    public Project update(Project project){
        if(!projectRepository.existsById(project.getProjectid()))
            return null;
        final var updatedProject = projectRepository.save(project);
        return updatedProject;
    }

    public boolean delete(Integer id) {
        if(!projectRepository.existsById(id))
            return false;
        projectRepository.deleteById(id);
        return true;
    }

    public Collection<Project> filterByName(String keyword){
        final var filteredProjects = projectRepository.findByProjectnameStartingWithIgnoreCase(keyword);
        return filteredProjects;
    }
}
