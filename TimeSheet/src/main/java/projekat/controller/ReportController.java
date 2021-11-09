package projekat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Report;
import projekat.models.TimeSheetEntry;
import projekat.services.ReportService;

@RestController
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}
	
	@GetMapping("report")
	public ResponseEntity<List<TimeSheetEntry>> getAllReports(@RequestBody Report report) {
		final var generatedReports = reportService.generateReport(report);
        return new ResponseEntity<>(generatedReports, HttpStatus.OK);
	}
	

	


}
