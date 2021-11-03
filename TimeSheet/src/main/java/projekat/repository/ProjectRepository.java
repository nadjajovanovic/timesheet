package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Project;

import java.util.Collection;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{

    Collection<Project> findByProjectnameStartingWithIgnoreCase(String name);
}
