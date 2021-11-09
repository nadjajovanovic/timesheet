package projekat.util;

import lombok.experimental.UtilityClass;
import projekat.models.*;

import java.util.Date;


@UtilityClass
public class BaseUT {

    public Category createTestCategory(String categoryName) {
        final var category = new Category();
        category.setCategoryname(categoryName);
        return category;
    }

    public Client createTestClient(String clientName) {
        final var client = new Client();
        client.setClientname(clientName);
        return client;
    }

    public Country createTestCountry(String countryName) {
        final var country = new Country();
        country.setCountryname(countryName);
        return country;
    }

    public Project createTestProject(String projectName, String projectDescription) {
        final var project = new Project();
        project.setProjectname(projectName);
        project.setProjectdescription(projectDescription);
        return project;
    }

    public Teammember createTeamMember(String teammemberName) {
        final var teammember = new Teammember();
        teammember.setTeammembername(teammemberName);
        return teammember;
    }

    public TimeSheetEntry createTestEntry(String description, Integer categoryid, Integer clientid, Integer projectId, Date entryDate) {
        final var entry = new TimeSheetEntry();
        entry.setDescription(description);
        entry.setClientid(clientid);
        entry.setProjectid(projectId);
        entry.setCategoryid(categoryid);
        entry.setTime(3.5);
        entry.setEntryDate(entryDate);
        return entry;
    }

}
