package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.models.Report;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    public ReportService(TimeSheetEntryRepository timeSheetEntryRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

    public List<TimeSheetEntry> generateReport(Report report) {
        final var allEntries = timeSheetEntryRepository.findAll();
        final var filteredReports =
                allEntries.stream()
                    .filter(e -> report.getProjectid() == null || e.getProjectid().equals(report.getProjectid()))
                    .filter(e -> report.getCategoryid() == null || e.getCategoryid().equals(report.getCategoryid()))
                    .filter(e -> report.getClientid() == null || e.getClientid().equals(report.getClientid()))
                    .filter(e -> report.getStartdate() == null || e.getEntryDate().after(report.getStartdate()))
                    .filter(e -> report.getEnddate() == null || e.getEntryDate().before(report.getEnddate()))
                    .toList();
        return filteredReports;
    }
}
