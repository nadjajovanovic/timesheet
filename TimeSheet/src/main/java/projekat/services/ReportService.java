package projekat.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.exception.BadRequestException;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import projekat.util.DateFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

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
