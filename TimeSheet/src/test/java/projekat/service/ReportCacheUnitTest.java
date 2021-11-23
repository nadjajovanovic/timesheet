package projekat.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import projekat.TimeSheetApplication;
import projekat.models.Report;
import projekat.models.TimeSheetEntry;
import projekat.repository.TimeSheetEntryRepository;
import projekat.services.ReportService;
import projekat.util.BaseUT;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TimeSheetApplication.class)
@AutoConfigureTestDatabase
@Disabled
public class ReportCacheUnitTest extends BaseUT {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ReportService service;

    @MockBean
    private TimeSheetEntryRepository entryRepository;

    @AfterEach
    public void cleanCache(){
        cacheManager.getCache("reports").clear();
    }

    @BeforeEach
    public void setUp() {
        final var firstEntry = new TimeSheetEntry();
        final var secondEntry = new TimeSheetEntry();
        final var entryList = new ArrayList<TimeSheetEntry>();
        entryList.add(firstEntry);
        entryList.add(secondEntry);
        Mockito.when(entryRepository.findAll())
                .thenReturn(entryList);
    }

    @Test
    void getCachedResults(){
        // Arrange
        final var report = new Report();
        final var reportHash = report.hashCode();
        service.generateReport(report);

        // Act
        final var cachedList = getCachedList(reportHash);

        // Assert
        assertEquals(2, cachedList.size());
    }

    @Test
    void getCachedResultsNoResults(){
        // Arrange
        final var report = new Report();
        final var reportHash = report.hashCode();

        // Act
        final var cachedList = getCachedList(reportHash);

        // Assert
        assertNull(cachedList);
    }

    @Test @Disabled
    void getCachedResultsAfter60sec() throws InterruptedException {
        // Arrange
        final var report = new Report();
        final var reportHash = report.hashCode();
        service.generateReport(report);

        // Act
        Thread.sleep(60000);
        final var cachedList = getCachedList(reportHash);

        // Assert
        assertNull(cachedList);
    }

    List<TimeSheetEntry> getCachedList(Integer hash) {
        final var cache = cacheManager.getCache("reports");
        final var object = cache.get(hash);
        try {
            final var listOfCashedEntries = (List<TimeSheetEntry>) object.get();
            return listOfCashedEntries;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
