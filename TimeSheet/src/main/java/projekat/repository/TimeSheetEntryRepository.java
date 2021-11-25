package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import projekat.models.TimeSheetEntry;

@Repository
public interface TimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Integer> {
    @Query("SELECT entry.teammemberid FROM TimeSheetEntry entry WHERE entry.entryId = ?1")
    Integer getTeamMemberIdOfEntry(Integer entryId);
}
