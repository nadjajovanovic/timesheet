package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import projekat.api.api.ReportApi;
import projekat.api.model.ReportFilterDTO;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.api.model.TimeSheetEntryReportDTO;
import projekat.exception.BadRequestException;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.services.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	@Override
	public ResponseEntity<Resource> exportToCSV(ReportFilterDTO reportFilterDTO) {
		final var resource = new ByteArrayOutputStream();
		final var headers = new HttpHeaders();
		final var headersKey = "Content-Disposition";
		final var dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		final var currentDateTime = dateFormat.format(new Date());
		final var headerValue = "attachment; filename=reports_" + currentDateTime + ".csv";
		headers.add(headersKey, headerValue);

		final var listOfGeneratedReports = reportService.generateReport(reportFilterDTO);
		final var filteredReports = listOfGeneratedReports
				.stream()
				.map(TimeSheetEntryMapper::toEntryForReportDTO)
				.toList();



		try (ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)){
			final var csvHeader = new String[]{ "Date", "Description", "Time", "Project", "Category", "Team member"};
			final var nameMapping = new String[]{ "date", "description", "totalTimeSpent", "projectName", "categoryName", "teamMemberName"};
			csvWriter.writeHeader(csvHeader);

			for (TimeSheetEntryReportDTO report : filteredReports) {
				csvWriter.write(report, nameMapping);
			}
		} catch (IOException ex) {
			throw new BadRequestException(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}

		final var res = new ByteArrayResource(resource.toByteArray());

		return new ResponseEntity(res, HttpStatus.OK);
	}



}
