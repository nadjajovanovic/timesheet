package projekat.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.exception.BadRequestException;
import projekat.models.Report;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    public ReportService(TimeSheetEntryRepository timeSheetEntryRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

    public List<TimeSheetEntry> getAll(){
        return timeSheetEntryRepository.findAll();
    }


    public List<TimeSheetEntry> generateReport(Report report) {

        final var reportKey = report.hashCode();
        final var array = redisCacheService.getFromCache(reportKey, List.class);

        if (array != null){
            final var cached = (List<TimeSheetEntry>)array;
            return cached;
        }

        final var allEntries = timeSheetEntryRepository.findAll();
        final var filteredReports =
                allEntries.stream()
                        .filter(e -> report.getProjectid() == null || e.getProjectid().equals(report.getProjectid()))
                        .filter(e -> report.getCategoryid() == null || e.getCategoryid().equals(report.getCategoryid()))
                        .filter(e -> report.getClientid() == null || e.getClientid().equals(report.getClientid()))
                        .filter(e -> report.getStartdate() == null || e.getEntryDate().after(report.getStartdate()))
                        .filter(e -> report.getEnddate() == null || e.getEntryDate().before(report.getEnddate()))
                        .toList();

        redisCacheService.storeInCache(reportKey, filteredReports);

        return filteredReports;
    }

    public InputStream createDocument(List<TimeSheetEntryReportDTO> timeSheetEntryReportDTOList) {

        final var document = new Document(PageSize.A4);
        final var out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document,  out);

            document.open();
            PdfPTable headerTable=new PdfPTable(7);
            headerTable.setWidthPercentage(98);
            PdfPCell cellValue = new PdfPCell(new Paragraph("Report id"));
            cellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValue.setNoWrap(true);
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Paragraph date"));
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Report description"));
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Report total time spent"));
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Report project name"));
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Report category name"));
            headerTable.addCell(cellValue);
            cellValue = new PdfPCell(new Paragraph("Report teammember name"));
            headerTable.addCell(cellValue);
            timeSheetEntryReportDTOList.forEach(item -> addReportToPdf(item,headerTable));
            document.add(headerTable);

            document.close();

        } catch (DocumentException e) {
            throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addReportToPdf(TimeSheetEntryReportDTO report, PdfPTable document){
        final var paragraphId = new Paragraph(report.getId().toString());
        document.addCell(paragraphId);
        final var paragraphDate = new Paragraph(report.getDate());
        document.addCell(paragraphDate);
        final var paragraphDescription = new Paragraph(report.getDescription());
        document.addCell(paragraphDescription);
        final var paragraphTotalTimeSpent = new Paragraph(String.valueOf(report.getTotalTimeSpent()));
        document.addCell(paragraphTotalTimeSpent);
        final var paragraphProjectName = new Paragraph( report.getProjectName());
        document.addCell(paragraphProjectName);
        final var paragraphCategoryName = new Paragraph( report.getCategoryName());
        document.addCell(paragraphCategoryName);
        final var paragraphTeamMemberName = new Paragraph( report.getTeamMemberName());
        document.addCell(paragraphTeamMemberName);
    }
}