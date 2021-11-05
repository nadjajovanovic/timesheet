package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import projekat.models.TimeSheetEntry;
import projekat.services.TimeSheetEntryService;

import java.sql.Time;
import java.util.Collection;

@RestController
public class TimeSheetEntryController {

    @Autowired
    private final TimeSheetEntryService timeSheetEntryService;

    public TimeSheetEntryController(TimeSheetEntryService timeSheetEntryService) {
        this.timeSheetEntryService = timeSheetEntryService;
    }

    @GetMapping("/entry")
    public ResponseEntity<Collection<TimeSheetEntry>> getAllEntries(){
        final var entries = timeSheetEntryService.getAll();
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping("/entry/{id}")
    public ResponseEntity<TimeSheetEntry> getOne(@PathVariable("id") Integer id){
        final var optionalEntry = timeSheetEntryService.getOne(id);
        if (optionalEntry.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalEntry.get(), HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/entry")
    public ResponseEntity<TimeSheetEntry> insertEntry(@RequestBody TimeSheetEntry entry){
        final var insertedEntry = timeSheetEntryService.create(entry);
        if (insertedEntry == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(insertedEntry, HttpStatus.CREATED);
    }

    @CrossOrigin
    @DeleteMapping("/entry/{id}")
    public ResponseEntity<TimeSheetEntry> deleteEntry(@PathVariable("id") Integer id){
        final var deleted = timeSheetEntryService.delete(id);
        if (!deleted){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
