package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.models.Report;
import projekat.repository.ReportRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Collection<Report> getAll() {
        final var reports = reportRepository.findAll();
        return reports;
    }

    public Optional<Report> getOne(Integer id) {
        final var oneReport = reportRepository.findById(id);
        return oneReport;
    }

    public Report insert(Report report) {
        final var inserted = reportRepository.save(report);
        return inserted;
    }

    public Report update(Report report) {
        if (!reportRepository.existsById(report.getReportid()))
            return null;
        final var updated = reportRepository.save(report);
        return updated;
    }

    public boolean delete(Integer id) {
        if (!reportRepository.existsById(id))
            return false;
        reportRepository.deleteById(id);
        return true;
    }
}
