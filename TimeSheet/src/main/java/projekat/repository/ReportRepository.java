package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekat.models.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>{

}
