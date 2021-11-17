package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.enums.ErrorCode;
import projekat.exception.NotFoundException;
import projekat.exception.InputFieldException;
import projekat.mapper.TimeSheetEntryMapper;
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
        if (!timeSheetEntryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
        }
        final var entry = timeSheetEntryRepository.findById(id);
        return entry;
    }

    public TimeSheetEntry create(TimeSheetEntryDTO dto){

        if (dto.getId() != null) {
            throw new InputFieldException("Id is present in request", ErrorCode.ID_EXISTS);
        }

        if (!categoryRepository.existsById(dto.getCategoryId())){
            throw new NotFoundException(String.format("Category with id %d does not exist in database", dto.getCategoryId()), ErrorCode.NOT_FOUND);
        }

        if (!clientRepository.existsById(dto.getClientId())) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", dto.getClientId()), ErrorCode.NOT_FOUND);
        }

        if (!projectRepository.existsById(dto.getProjectId())) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", dto.getProjectId()), ErrorCode.NOT_FOUND);
        }


        final var entry = TimeSheetEntryMapper.fromEntryDTO(dto);

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
        if (entry.getEntryId() == null) {
            throw new InputFieldException("Id is not present in request", ErrorCode.ID_NOT_FOUND);
        }
        if (!timeSheetEntryRepository.existsById(entry.getEntryId())){
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", entry.getEntryId()), ErrorCode.NOT_FOUND);
        }

        if (!categoryRepository.existsById(entry.getCategoryid())){
            throw new NotFoundException(String.format("Category with id %d does not exist in database", entry.getCategoryid()), ErrorCode.NOT_FOUND);
        }

        if (!clientRepository.existsById(entry.getClientid())) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", entry.getClientid()), ErrorCode.NOT_FOUND);
        }

        if (!projectRepository.existsById(entry.getProjectid())) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", entry.getProjectid()), ErrorCode.NOT_FOUND);
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
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", id), ErrorCode.NOT_FOUND);
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
