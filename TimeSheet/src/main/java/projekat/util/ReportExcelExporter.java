package projekat.util;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import projekat.api.model.TimeSheetEntryReportDTO;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

public class ReportExcelExporter {

    private final XSSFWorkbook workbook;
    private final List<TimeSheetEntryReportDTO> report;
    private XSSFSheet sheet;
    private int rowCount;

    public ReportExcelExporter(List<TimeSheetEntryReportDTO> report) {
        workbook = new XSSFWorkbook();
        this.report = report;
        rowCount = 0;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Time Sheet");
        final var row = sheet.createRow(rowCount);

        final var style = workbook.createCellStyle();
        final var font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(10);
        style.setFont(font);

        createCell(row, 0, "Date", style);
        createCell(row, 1, "Team member", style);
        createCell(row, 2, "Projects", style);
        createCell(row, 3, "Categories", style);
        createCell(row, 4, "Description", style);
        createCell(row, 5, "Time", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        final var cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof BigDecimal) {
            final var doubleValue = ((BigDecimal) value).doubleValue();
            cell.setCellValue(doubleValue);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        rowCount = 1;

        final var style = workbook.createCellStyle();
        for (TimeSheetEntryReportDTO entry : report) {
            final var row = sheet.createRow(rowCount++);
            var columnCount = 0;

            createCell(row, columnCount++, entry.getDate(), style);
            createCell(row, columnCount++, entry.getTeamMemberName(), style);
            createCell(row, columnCount++, entry.getProjectName(), style);
            createCell(row, columnCount++, entry.getCategoryName(), style);
            createCell(row, columnCount++, entry.getDescription(), style);
            createCell(row, columnCount, entry.getTotalTimeSpent(), style);
        }
    }

    private void writeTotalLine() {
        final var style = workbook.createCellStyle();
        final var font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        final var row = sheet.createRow(++rowCount);
        final var totalHours = report
                .stream()
                .mapToDouble(e -> e.getTotalTimeSpent().doubleValue())
                .sum();
        createCell(row, 4, "Total:", style);
        createCell(row, 5, totalHours, style);
    }

    @SneakyThrows
    public void export(OutputStream stream){
        writeHeaderLine();
        writeDataLines();
        writeTotalLine();
        workbook.write(stream);
        workbook.close();
    }

}
