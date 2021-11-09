package projekat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private Integer categoryid;

    private String categoryname;

    private List<ReportDTO> reports;
}
