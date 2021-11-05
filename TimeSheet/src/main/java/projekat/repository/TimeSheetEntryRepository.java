package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekat.models.TimeSheetEntry;

@Repository
public interface TimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Integer> {
}
