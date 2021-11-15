package projekat.util;

import projekat.enums.ProjectStatus;
import projekat.models.*;

import java.util.Date;


public class BaseUT {

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

    protected TimeSheetEntry createTestEntry(String description, Integer categoryid, Integer clientid, Integer projectId, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        entry.setClientid(clientid);
        entry.setProjectid(projectId);
        entry.setCategoryid(categoryid);
        entry.setTime(3.5);
        entry.setEntryDate(entryDate);
        return entry;
    }

    protected TimeSheetEntry createTestEntryWithObjects(String description, Category category, Client client, Project project, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        entry.setClient(client);
        entry.setClientid(client.getClientid());
        entry.setProject(project);
        entry.setProjectid(project.getProjectid());
        entry.setCategory(category);
        entry.setCategoryid(category.getCategoryid());
        entry.setTime(3.5);
        entry.setEntryDate(entryDate);
        return entry;
    }
}
