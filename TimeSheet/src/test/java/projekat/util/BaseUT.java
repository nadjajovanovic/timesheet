package projekat.util;

import org.springframework.beans.factory.annotation.Autowired;
import projekat.enums.ProjectStatus;
import projekat.enums.TeamMemberRoles;
import projekat.models.*;
import projekat.repository.TeamMemberRepository;

import java.util.Date;


public class BaseUT {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    protected TestAuthFactory testAuthFactory;

    protected Category createTestCategory(String categoryName) {
        final var category = new Category();
        category.setCategoryname(categoryName);
        return category;
    }

    protected Client createTestClient(String clientName) {
        final var client = new Client();
        client.setClientname(clientName);
        return client;
    }

    protected Country createTestCountry(String countryName) {
        final var country = new Country();
        country.setCountryname(countryName);
        return country;
    }

    protected Project createTestProject(String projectName, String projectDescription) {
        final var project = new Project();
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);
        project.setStatus(ProjectStatus.Active);
        return project;
    }

    protected Teammember createTeamMember(String teammemberName) {
        final var teammember = new Teammember();
        teammember.setTeammembername(teammemberName);
        return teammember;
    }

    protected TimeSheetEntry createTestEntry(String description, Integer categoryid, Integer clientid, Integer projectId,
                                              Integer teamMmberId, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        entry.setClientid(clientid);
        entry.setTeammemberid(teamMmberId);
        entry.setProjectid(projectId);
        entry.setCategoryid(categoryid);
        entry.setTime(3.5);
        entry.setEntryDate(entryDate);
        return entry;
    }

    protected TimeSheetEntry createTestEntryWithObjects(String description, Category category, Client client,
                                                        Project project, Teammember teammember, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        entry.setClient(client);
        entry.setClientid(client.getClientid());
        entry.setProject(project);
        entry.setProjectid(project.getProjectid());
        entry.setCategory(category);
        entry.setCategoryid(category.getCategoryid());
        entry.setTeammember(teammember);
        entry.setTeammemberid(teammember.getTeammemberid());
        entry.setTime(3.5);
        entry.setEntryDate(entryDate);
        return entry;
    }


    protected Teammember saveTeamMember() { // save into DB
        final var teammember = new Teammember();
        teammember.setTeammembername("name");
        teammember.setPassword("$2a$10$oUvS02vbxyTUe3J5ZlGV8e4lM2Rnkdfcvcc9cXAtQYCbxq3rfgiKe");
        teammember.setUsername("adminTest");
        teammember.setEmail("test@example.com");
        teammember.setStatus(true);
        teammember.setRole(TeamMemberRoles.ROLE_ADMIN);
        teammember.setHoursperweek(2.3);
        return teamMemberRepository.saveAndFlush(teammember);
    }
}
