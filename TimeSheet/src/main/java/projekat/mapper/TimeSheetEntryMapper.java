package projekat.mapper;

import projekat.api.model.TimeSheetEntryDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.models.TimeSheetEntry;
import projekat.util.DateFormatter;

import java.math.BigDecimal;

public class TimeSheetEntryMapper {


    public static TimeSheetEntryReportDTO toEntryForReportDTO(TimeSheetEntry entry) {
        final var dto = new TimeSheetEntryReportDTO();
        final var totalTime = (entry.getOvertime() == null || entry.getOvertime() < 0 || entry.getOvertime() > 24) ?
                BigDecimal.valueOf(entry.getTime()) :
                BigDecimal.valueOf(entry.getTime()).add(BigDecimal.valueOf(entry.getOvertime()));
        dto.setId(entry.getEntryId());
        dto.setDescription(entry.getDescription());
        dto.setDate(DateFormatter.dateToString(entry.getEntryDate()));
        dto.setTotalTimeSpent(totalTime);
        dto.setProjectName(entry.getProject().getProjectname());
        dto.setCategoryName(entry.getCategory().getCategoryname());
        // TODO add team member name when authentication is done
        dto.setTeamMemberName(null);
        return dto;
    }

    public static TimeSheetEntryDTO toEntryDTO(TimeSheetEntry entry) {
        final var dto = new TimeSheetEntryDTO();
        dto.setId(entry.getEntryId());
        dto.setDescription(entry.getDescription());
        dto.setDate(DateFormatter.dateToString(entry.getEntryDate()));
        dto.setTimeSpent(BigDecimal.valueOf(entry.getTime()));
        dto.setProjectId(entry.getProjectid());
        dto.setCategoryId(entry.getCategoryid());
        // TODO add team member name when authentication is done
        dto.setClientId(entry.getClientid());
        return dto;
    }

    public static TimeSheetEntry fromEntryDTO(TimeSheetEntryDTO dto){
        final var entry = new TimeSheetEntry();
        entry.setEntryId(dto.getId());
        entry.setDescription(dto.getDescription());
        entry.setEntryDate(DateFormatter.stringToDate(dto.getDate()));
        entry.setTime(dto.getTimeSpent().doubleValue());
        entry.setProjectid(dto.getProjectId());
        entry.setCategoryid(dto.getCategoryId());
        // TODO add team member name when authentication is done
        entry.setClientid(dto.getClientId());
        return entry;
    }
}

