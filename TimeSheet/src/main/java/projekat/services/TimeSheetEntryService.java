package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.models.Category;
import projekat.models.Client;
import projekat.models.Project;
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

        if (!categoryRepository.existsById(entry.getCategoryid())
                || !clientRepository.existsById(entry.getClientid())
                || !projectRepository.existsById(entry.getProjectid())){
            return null;
        }

        final var emptyCategory = createEmptyCategory(entry.getCategoryid());
        entry.setCategory(emptyCategory);
        final var emptyClient = createEmptyClient(entry.getClientid());
        entry.setClient(emptyClient);
        final var emptyProject = createEmptyProject(entry.getProjectid());
        entry.setProject(emptyProject);
        final var insertedEntry = timeSheetEntryRepository.save(entry);
        return insertedEntry;
    }

    public TimeSheetEntry update(TimeSheetEntry entry) {
        if (!timeSheetEntryRepository.existsById(entry.getEntryId())){
            return null;
        }

        if (!categoryRepository.existsById(entry.getCategoryid())
                || !clientRepository.existsById(entry.getClientid())
                || !projectRepository.existsById(entry.getProjectid())){
            return null;
        }

        final var emptyCategory = createEmptyCategory(entry.getCategoryid());
        entry.setCategory(emptyCategory);
        final var emptyClient = createEmptyClient(entry.getClientid());
        entry.setClient(emptyClient);
        final var emptyProject = createEmptyProject(entry.getProjectid());
        entry.setProject(emptyProject);

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

    private Category createEmptyCategory(Integer categoryId) {
        final var category = new Category();
        category.setCategoryid(categoryId);
        return category;
    }

    private Client createEmptyClient(Integer clientId) {
        final var client = new Client();
        client.setClientid(clientId);
        return client;
    }

    private Project createEmptyProject(Integer projectId) {
        final var project = new Project();
        project.setProjectid(projectId);
        return project;
    }
}
