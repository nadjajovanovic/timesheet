package projekat.dto;

import java.util.Date;

public class TimeSheetEntryDTO {
    private Integer entryId;

    private Date entryDate;

    private ClientDTO client;

    private ProjectDTO project;

    private CategoryDTO category;

    private Integer clientid;

    private Integer projectid;

    private Integer categoryid;

    private String description;

    private Double time;

    private Double overtime;
}
