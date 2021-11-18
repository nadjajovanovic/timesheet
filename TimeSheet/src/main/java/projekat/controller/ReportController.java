package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import projekat.api.api.ReportApi;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.models.TimeSheetEntry;
import projekat.services.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ReportController implements ReportApi {

	@Autowired
	private ReportService reportService;

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

	@PostMapping("report/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("application/csv");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateTime = dateFormat.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=reports_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);

		final var reportFilter = new ReportFilterDTO();
		final var listOfGeneratedReports = reportService.generateReport(reportFilter);
		final var filteredReports = listOfGeneratedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = { "Date", "Description", "Time", "Project", "Category", "Team member"};
		String[] nameMapping = { "date", "description", "totalTimeSpent", "projectName", "categoryName", "teamMemberName"};

		csvWriter.writeHeader(csvHeader);

		for (TimeSheetEntryReportDTO report : filteredReports) {
			csvWriter.write(report, nameMapping);
		}

		csvWriter.close();
	}



}
