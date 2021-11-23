package projekat.mapper;

import projekat.api.model.ReportFilterDTO;
import projekat.models.Report;
import projekat.util.DateFormatter;

public class ReportMapper {

    public static Report toReport (ReportFilterDTO dto) {
        final var report = new Report();
        report.setCategoryid(dto.getCategoryId());
        report.setProjectid(dto.getProjectId());
        report.setTeammemberid(dto.getTeamMemberId());
        report.setClientid(dto.getClientId());
        if (dto.getStartDate() != null){
            final var startDate = DateFormatter.stringToDate(dto.getStartDate());
            report.setStartdate(startDate);
        }
        if (dto.getEndDate() != null){
            final var endDate = DateFormatter.stringToDate(dto.getEndDate());
            report.setEnddate(endDate);
        }
        return report;
    }
}
