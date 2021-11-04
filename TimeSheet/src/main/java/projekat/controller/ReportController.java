package projekat.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Client;
import projekat.models.Report;
import projekat.repository.ReportRepository;
import projekat.services.ReportService;

@RestController
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}
	
	@GetMapping("report")
	public ResponseEntity<Collection<Report>> getAllReports() {
		final var reports = reportService.getAll();
		return new ResponseEntity<>(reports, HttpStatus.OK);
	}
	
	@GetMapping("report/{reportid}")
	public ResponseEntity<Report> getReport(@PathVariable Integer reportid) {
		final var oneReport = reportService.getOne(reportid);
		if (oneReport.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(oneReport.get(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("report")
	public ResponseEntity<Report> insertReport(@RequestBody Report report) {
		if (report.getReportid() != null) {
			return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var inserted = reportService.insert(report);
		return new ResponseEntity<Report>(inserted, HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@PutMapping("report")
	public ResponseEntity<Report> updateReport(@RequestBody Report report) {
		if (report.getReportid() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updated = reportService.update(report);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("report/{reportid}")
	public ResponseEntity<Report> deleteReport(@PathVariable Integer reportid) {
		final var deleted = reportService.delete(reportid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
