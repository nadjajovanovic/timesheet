package projekat.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import projekat.util.DateFormatter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    public static final String fileName = "timeSheetEntryReport.pdf";

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    public ReportService(TimeSheetEntryRepository timeSheetEntryRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

    public List<TimeSheetEntry> getAll(){
        return timeSheetEntryRepository.findAll();
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

    public Document createDocument(List<TimeSheetEntryReportDTO> timeSheetEntryReportDTOList) {

        final var document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            timeSheetEntryReportDTOList.forEach(item -> addReportToPdf(item,document));
            document.close();

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return document;
    }

    private void addReportToPdf(TimeSheetEntryReportDTO report, Document document){
        try {
            final var paragraphId = new Paragraph("Report id: " + report.getId());
            document.add(paragraphId);
            final var paragrphDate = new Paragraph("Paragraph date: " + report.getDate());
            document.add(paragrphDate);
            final var paragrphDescription = new Paragraph("Report description: " + report.getDescription());
            document.add(paragrphDescription);
            final var paragrphTotalTimeSpent = new Paragraph("Report total time spent: " + report.getTotalTimeSpent());
            document.add(paragrphTotalTimeSpent);
            final var paragrphProjectName = new Paragraph("Report project name: " + report.getProjectName());
            document.add(paragrphProjectName);
            final var paragrphCategoryName = new Paragraph("Report category name: " + report.getCategoryName());
            document.add(paragrphCategoryName);
            final var paragrphTeamMemberName = new Paragraph("Report teammember name: " + report.getTeamMemberName());
            document.add(paragrphTeamMemberName);
            document.add( Chunk.NEWLINE );
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
