package projekat.dto;

import projekat.enums.ProjectStatus;

import java.util.List;

public class ProjectDTO {
    private Integer projectid;

    private String projectdescription;

    private String projectname;

    private ClientDTO client;

    private ProjectStatus status;

    private List<TimeSheetEntryDTO> entries;
}
