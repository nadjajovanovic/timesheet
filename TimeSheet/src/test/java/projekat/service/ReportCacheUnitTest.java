package projekat.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import projekat.TimeSheetApplication;
import projekat.models.Report;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import projekat.services.RedisCacheService;
import projekat.services.ReportService;
import projekat.util.BaseUT;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TimeSheetApplication.class)
class ReportCacheUnitTest extends BaseUT {

    @Autowired
    private ReportService service;

    @MockBean
    private RedisCacheService cacheService;

    @MockBean
    private TimeSheetEntryRepository entryRepository;

    @Test
    void getCachedResultsExistsInCache(){
        // Arrange
        final var report = new Report();
        final var reportHash = report.hashCode();
        final var firstEntry = new TimeSheetEntry();
        final var secondEntry = new TimeSheetEntry();
        final var entryList = new ArrayList<TimeSheetEntry>();
        entryList.add(firstEntry);
        entryList.add(secondEntry);
        Mockito.when(entryRepository.findAll())
                .thenReturn(entryList);
        Mockito.when(cacheService.getFromCache(reportHash, List.class))
                .thenReturn(entryList);

        // Act
        service.generateReport(report);
        service.generateReport(report);

        // Assert
        verify(cacheService, times(2)).getFromCache(report.hashCode(), List.class);
    }

    @Test
    void getCachedResultsNoResultsInCache() {
        // Arrange
        final var report = new Report();
        report.setCategoryid(1);
        final var reportHash = report.hashCode();
        final var entry = new TimeSheetEntry();
        entry.setCategoryid(1);
        final var entryList = new ArrayList<TimeSheetEntry>();
        entryList.add(entry);
        Mockito.when(entryRepository.findAll())
                .thenReturn(entryList);
        Mockito.when(cacheService.getFromCache(reportHash, List.class))
                .thenReturn(null);

        // Act
        service.generateReport(report);

        // Assert
        verify(cacheService, times(1)).getFromCache(report.hashCode(), List.class);
        verify(entryRepository, times(1)).findAll();
    }
}
