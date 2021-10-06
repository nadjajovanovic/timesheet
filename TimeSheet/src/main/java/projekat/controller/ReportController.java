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

import projekat.models.Report;
import projekat.repository.ReportRepository;

@RestController
public class ReportController {
	
	@Autowired
	private ReportRepository reportRepository;
	
	public ReportController(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}
	
	@GetMapping("report")
	public Collection<Report> getAllReports() {
		return reportRepository.findAll();
	}
	
	@GetMapping("report/{reportid}")
	public Report getReport(@PathVariable Integer reportid) {
		return reportRepository.getById(reportid);
	}
	
	@CrossOrigin
	@PostMapping("report")
	public ResponseEntity<Report> insertReport(@RequestBody Report report) {
		reportRepository.save(report);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("report")
	public ResponseEntity<Report> updateReport(@RequestBody Report report) {
		if(reportRepository.existsById(report.getReportid()))
			reportRepository.save(report);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("report/{reportid}")
	public ResponseEntity<Report> deleteReport(@PathVariable Integer reportid) {
		if (reportRepository.existsById(reportid))
			reportRepository.deleteById(reportid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
