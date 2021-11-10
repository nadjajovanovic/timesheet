package projekat.mapper;

import projekat.api.model.TimeSheetEntryDTO;
import projekat.models.TimeSheetEntry;

import java.math.BigDecimal;

public class TimeSheetEntryMapper {
    public static TimeSheetEntryDTO toEntryDTO(TimeSheetEntry entry){
        final var dto = new TimeSheetEntryDTO();
        final var totalTime = (entry.getOvertime() == null || entry.getOvertime() < 0 || entry.getOvertime() > 24)?
                BigDecimal.valueOf(entry.getTime()):
                BigDecimal.valueOf(entry.getTime()).add(BigDecimal.valueOf(entry.getOvertime()));
        dto.setId(entry.getEntryId());
        dto.setDescription(entry.getDescription());
        dto.setDate(entry.getEntryDate().toString());
        dto.setTotalTimeSpent(totalTime);
        dto.setProjectName(entry.getProject().getProjectname());
        dto.setCategoryName(entry.getCategory().getCategoryname());
        // TODO add team member name when authentication is done
        dto.setTeamMemberName(null);
        return dto;
    }
}
