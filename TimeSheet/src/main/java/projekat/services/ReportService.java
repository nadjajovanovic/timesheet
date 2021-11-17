package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.api.model.ReportFilterDTO;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import projekat.util.DateFormatter;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    public ReportService(TimeSheetEntryRepository timeSheetEntryRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

    public List<TimeSheetEntry> generateReport(ReportFilterDTO report) {
        final var allEntries = timeSheetEntryRepository.findAll();

        Date date1 = null;
        Date date2 = null;

        if (report.getStartDate() != null){
            date1 = DateFormatter.stringToDate(report.getStartDate());
        }
        if (report.getEndDate() != null){
            date2 = DateFormatter.stringToDate(report.getEndDate());
        }

        final var finalDate1 = date1;
        final var finalDate2 = date2;
        final var filteredReports =
                allEntries.stream()
                    .filter(e -> report.getProjectId() == null || e.getProjectid().equals(report.getProjectId()))
                    .filter(e -> report.getCategoryId() == null || e.getCategoryid().equals(report.getCategoryId()))
                    .filter(e -> report.getClientId() == null || e.getClientid().equals(report.getClientId()))
                    .filter(e -> report.getStartDate() == null || finalDate1 == null || e.getEntryDate().after(finalDate1))
                    .filter(e -> report.getEndDate() == null || finalDate2 == null || e.getEntryDate().before(finalDate2))
                    .toList();
        return filteredReports;
    }
}
