package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import projekat.api.model.TimeSheetEntryDTO;
import projekat.exception.BadRequestException;
import projekat.exception.InputFieldException;
import projekat.exception.NotFoundException;
import projekat.mapper.TimeSheetEntryMapper;
import projekat.models.*;
import projekat.repository.*;

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

    @Autowired
    private final TeamMemberRepository teamMemberRepository;


    public TimeSheetEntryService(TimeSheetEntryRepository timeSheetEntryRepository, ClientRepository clientRepository,
                                    ProjectRepository projectRepository, CategoryRepository categoryRepository,
                                 TeamMemberRepository teamMemberRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public Collection<TimeSheetEntry> getAll() {
        final var entries = timeSheetEntryRepository.findAll();
        return entries;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public Optional<TimeSheetEntry> getOne(Integer id) {
        if (!timeSheetEntryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var entry = timeSheetEntryRepository.findById(id);
        return entry;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public TimeSheetEntry create(TimeSheetEntryDTO dto){

        if (dto.getId() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }

        if (!categoryRepository.existsById(dto.getCategoryId())){
            throw new NotFoundException(String.format("Category with id %d does not exist in database", dto.getCategoryId()), HttpStatus.NOT_FOUND);
        }

        if (!clientRepository.existsById(dto.getClientId())) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", dto.getClientId()), HttpStatus.NOT_FOUND);
        }

        if (!projectRepository.existsById(dto.getProjectId())) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", dto.getProjectId()), HttpStatus.NOT_FOUND);
        }

        if (!teamMemberRepository.existsById(dto.getTeamMemberId())) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", dto.getTeamMemberId()), HttpStatus.NOT_FOUND);
        }

        final var entry = TimeSheetEntryMapper.fromEntryDTO(dto);

        final var emptyCategory = createEmptyCategory(entry.getCategoryid());
        entry.setCategory(emptyCategory);
        final var emptyClient = createEmptyClient(entry.getClientid());
        entry.setClient(emptyClient);
        final var emptyProject = createEmptyProject(entry.getProjectid());
        entry.setProject(emptyProject);
        final var emptyTeamMember = createEmptyTeamMember(entry.getTeammemberid());
        entry.setTeammember(emptyTeamMember);
        final var insertedEntry = timeSheetEntryRepository.save(entry);
        return insertedEntry;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public TimeSheetEntry update(TimeSheetEntry entry) {
        if (entry.getEntryId() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.NOT_FOUND);
        }
        if (!timeSheetEntryRepository.existsById(entry.getEntryId())){
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", entry.getEntryId()), HttpStatus.NOT_FOUND);
        }
        if (!categoryRepository.existsById(entry.getCategoryid())){
            throw new NotFoundException(String.format("Category with id %d does not exist in database", entry.getCategoryid()), HttpStatus.NOT_FOUND);
        }

        if (!clientRepository.existsById(entry.getClientid())) {
            throw new NotFoundException(String.format("Client with id %d does not exist in database", entry.getClientid()), HttpStatus.NOT_FOUND);
        }

        if (!projectRepository.existsById(entry.getProjectid())) {
            throw new NotFoundException(String.format("Project with id %d does not exist in database", entry.getProjectid()), HttpStatus.NOT_FOUND);
        }

        final var entryTeamMemberId = timeSheetEntryRepository.getTeamMemberIdOfEntry(entry.getEntryId());
        if(!entry.getTeammemberid().equals(entryTeamMemberId)){
            throw new BadRequestException("You are not allowed to change this entry", HttpStatus.FORBIDDEN);
        }

        final var emptyCategory = createEmptyCategory(entry.getCategoryid());
        entry.setCategory(emptyCategory);
        final var emptyClient = createEmptyClient(entry.getClientid());
        entry.setClient(emptyClient);
        final var emptyProject = createEmptyProject(entry.getProjectid());
        entry.setProject(emptyProject);
        final var emptyTeamMember = createEmptyTeamMember(entry.getTeammemberid());
        entry.setTeammember(emptyTeamMember);

        final var updatedEntry = timeSheetEntryRepository.save(entry);
        return updatedEntry;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete (Integer id) {
        if (!timeSheetEntryRepository.existsById(id)){
            throw new NotFoundException(String.format("Timesheet entry with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
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

    private Teammember createEmptyTeamMember(Integer userId) {
        final var user = new Teammember();
        user.setTeammemberid(userId);
        return user;
    }
}
