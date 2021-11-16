package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.EntryApi;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.models.TimeSheetEntry;
import projekat.services.TimeSheetEntryService;

import java.text.ParseException;
import java.util.List;

@RestController
public class TimeSheetEntryController implements EntryApi {

    @Autowired
    private final TimeSheetEntryService timeSheetEntryService;

    public TimeSheetEntryController(TimeSheetEntryService timeSheetEntryService) {
        this.timeSheetEntryService = timeSheetEntryService;
    }

    @Override
    public ResponseEntity<List<TimeSheetEntryDTO>> getAllEntries() {
        final var entries = timeSheetEntryService.getAll();
        final var dtoEntries =
                entries.stream()
                        .map(TimeSheetEntryMapper::toEntryDTO)
                        .toList();
        return new ResponseEntity<>(dtoEntries, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TimeSheetEntryDTO> getEntry(@PathVariable("id") Integer id) {
        final var optionalEntry = timeSheetEntryService.getOne(id);
        if (optionalEntry.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final var entryDTO = TimeSheetEntryMapper.toEntryDTO(optionalEntry.get());
        return new ResponseEntity<>(entryDTO, HttpStatus.OK);
    }

    @CrossOrigin
    @Override
    public ResponseEntity<TimeSheetEntryDTO> insertEntry(@RequestBody TimeSheetEntryDTO entry) {
        TimeSheetEntry insertedEntry = null;
        try {
            insertedEntry = timeSheetEntryService.create(entry);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (insertedEntry == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final var entryDTO = TimeSheetEntryMapper.toEntryDTO(insertedEntry);
        return new ResponseEntity<>(entryDTO, HttpStatus.CREATED);
    }

    @CrossOrigin
    @Override
    public ResponseEntity<TimeSheetEntryDTO> updateEntry(@RequestBody TimeSheetEntryDTO entryDTO) {
        TimeSheetEntry entry = null;
        try {
            entry = TimeSheetEntryMapper.fromEntryDTO(entryDTO);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final var updatedEntry = timeSheetEntryService.update(entry);
        if (updatedEntry == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final var updatedDTO = TimeSheetEntryMapper.toEntryDTO(updatedEntry);
        return new ResponseEntity<>(updatedDTO, HttpStatus.OK);
    }

    @CrossOrigin
    @Override
    public ResponseEntity deleteEntry(@PathVariable("id") Integer id) {
        final var deleted = timeSheetEntryService.delete(id);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
