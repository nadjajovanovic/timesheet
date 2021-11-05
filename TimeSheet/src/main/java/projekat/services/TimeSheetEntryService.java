package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.models.TimeSheetEntry;
import projekat.repository.CategoryRepository;
import projekat.repository.ClientRepository;
import projekat.repository.ProjectRepository;
import projekat.repository.TimeSheetEntryRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class TimeSheetEntryService {

    @Autowired
    private final TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    private final ClientRepository clientRepository;

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    public TimeSheetEntryService(TimeSheetEntryRepository timeSheetEntryRepository, ClientRepository clientRepository,
                                    ProjectRepository projectRepository, CategoryRepository categoryRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
    }

    public Collection<TimeSheetEntry> getAll() {
        final var entries = timeSheetEntryRepository.findAll();
        return entries;
    }

    public Optional<TimeSheetEntry> getOne(Integer id) {
        final var entry = timeSheetEntryRepository.findById(id);
        return entry;
    }

    public TimeSheetEntry create(TimeSheetEntry entry) {
        final var category = categoryRepository.findById(entry.getCategory().getCategoryid());
        final var client = clientRepository.findById(entry.getClient().getClientid());
        final var project = projectRepository.findById(entry.getProject().getProjectid());

        if (category.isPresent() && client.isPresent() && project.isPresent()){
            entry.setCategory(category.get());
            entry.setProject(project.get());
            entry.setClient(client.get());
        } else {
            return null;
        }
        final var insertedEntry = timeSheetEntryRepository.save(entry);
        return insertedEntry;
    }

    public TimeSheetEntry update(TimeSheetEntry entry) {
        if (!timeSheetEntryRepository.existsById(entry.getEntryId())){
            return null;
        }

        final var category = categoryRepository.findById(entry.getCategory().getCategoryid());
        final var client = clientRepository.findById(entry.getClient().getClientid());
        final var project = projectRepository.findById(entry.getProject().getProjectid());

        if (category.isPresent() && client.isPresent() && project.isPresent()){
            entry.setCategory(category.get());
            entry.setProject(project.get());
            entry.setClient(client.get());
        } else {
            return null;
        }
        final var updatedEntry = timeSheetEntryRepository.save(entry);
        return updatedEntry;
    }

    public boolean delete (Integer id) {
        if (!timeSheetEntryRepository.existsById(id)){
            return false;
        }
        timeSheetEntryRepository.deleteById(id);
        return true;
    }
}
