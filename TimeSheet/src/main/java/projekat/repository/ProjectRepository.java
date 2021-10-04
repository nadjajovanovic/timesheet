package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{

}
