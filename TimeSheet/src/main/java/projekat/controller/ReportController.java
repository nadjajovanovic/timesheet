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
import projekat.api.api.ReportApi;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.exception.BadRequestException;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.services.ReportService;
import projekat.util.ReportExcelExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class ReportController implements ReportApi {

	@Autowired
	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@Override
	public ResponseEntity<List<TimeSheetEntryDTO>> getRequiredReports(@RequestBody ReportFilterDTO report) {
		final var generatedReports = reportService.generateReport(report);
		final var filtered = generatedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();
		return new ResponseEntity(filtered, HttpStatus.OK);
	}


	@Override
	public ResponseEntity<Resource> getReportsInPdf() {
		final var report = new ReportFilterDTO();
		final var headers = new HttpHeaders();
		final var headerKey = "Content-Disposition";
		final var headerValue = "attachment; filename=report.pdf";
		headers.add(headerKey, headerValue);
		byte[] contents;
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

		final var generatedReports = reportService.generateReport(reportFilterDTO);
		final var filtered = generatedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();

		final var excelExporter = new ReportExcelExporter(filtered);
		excelExporter.export(resource);
		final var res = new ByteArrayResource(resource.toByteArray());

		return new ResponseEntity(res, headers, HttpStatus.OK);
	}
}
