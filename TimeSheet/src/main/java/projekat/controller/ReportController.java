package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.ReportApi;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.services.ReportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

		byte[] contents = new byte[0];
		final var timeSheetEntryReportDTOList = reportService.generateReport(report)
			   .stream()
			   .map(TimeSheetEntryMapper::toEntryForReportDTO)
			   .toList();

		reportService.createDocument(timeSheetEntryReportDTOList);
		File file = new File(ReportService.fileName);
		String path = file.getAbsolutePath();
		try {
			contents = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity(contents, HttpStatus.OK);
	}
}
