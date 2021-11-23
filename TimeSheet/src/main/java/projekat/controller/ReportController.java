package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import projekat.api.api.ReportApi;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.exception.BadRequestException;
import projekat.mapper.ReportMapper;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.services.ReportService;
import projekat.util.ReportExcelExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class ReportController implements ReportApi {

	@Autowired
	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@Override
	public ResponseEntity<List<TimeSheetEntryDTO>> getRequiredReports(@RequestBody ReportFilterDTO dto) {
		final var report = ReportMapper.toReport(dto);
		final var generatedReports = reportService.generateReport(report);

		final var filtered = generatedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();
		return new ResponseEntity(filtered, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> getReportsInPdf() {
		final var reportDTO = new ReportFilterDTO();
		final var headers = new HttpHeaders();
		final var headerKey = "Content-Disposition";
		final var headerValue = "attachment; filename=report.pdf";
		headers.add(headerKey, headerValue);
		byte[] contents;
		final var report = ReportMapper.toReport(reportDTO);
		final var timeSheetEntryReportDTOList = reportService.generateReport(report)
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();

		final var inputStream=reportService.createDocument(timeSheetEntryReportDTOList);
		try {
			contents = inputStream.readAllBytes();
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(contents,headers ,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> getExcelReport(ReportFilterDTO reportFilterDTO) {
		final var resource = new ByteArrayOutputStream();
		final var headers = new HttpHeaders();
		final var headerKey = "Content-Disposition";
		final var headerValue = "attachment; filename=report.xlsx";
		headers.add(headerKey, headerValue);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		final var report = ReportMapper.toReport(reportFilterDTO);
		final var generatedReports = reportService.generateReport(report);
		final var filtered = generatedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();

		final var excelExporter = new ReportExcelExporter(filtered);
		excelExporter.export(resource);
		final var res = new ByteArrayResource(resource.toByteArray());

		return new ResponseEntity(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> exportToCSV(ReportFilterDTO reportFilterDTO) {
		final var resource = new ByteArrayOutputStream();
		final var headersKey = "Content-Disposition";
		final var headerValue = "attachment; filename=reports.csv";
		final var headers = new HttpHeaders();
		headers.add(headersKey, headerValue);

		final var reportObject = ReportMapper.toReport(reportFilterDTO);
		final var listOfGeneratedReports = reportService.generateReport(reportObject);
		final var filteredReports = listOfGeneratedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();

		try {
			var writer = new OutputStreamWriter(resource, StandardCharsets.UTF_8);
			try (var csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {
				final var csvHeader = new String[]{"Date", "Description", "Time", "Project", "Category", "Team member"};
				final var nameMapping = new String[]{"date", "description", "totalTimeSpent", "projectName", "categoryName", "teamMemberName"};
				csvWriter.writeHeader(csvHeader);

				for (var report : filteredReports) {
					csvWriter.write(report, nameMapping);
				}
			}
		} catch (IOException ex) {
			throw new BadRequestException(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		final var res = new ByteArrayResource(resource.toByteArray());

		return new ResponseEntity(res, headers, HttpStatus.OK);
	}
}
