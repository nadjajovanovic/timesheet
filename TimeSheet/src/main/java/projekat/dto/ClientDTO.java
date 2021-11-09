package projekat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {

    private Integer clientid;

    private String clientaddress;

    private String clientcity;

    private String clientname;

    private String clientzipcode;

    private CountryDTO country;

    private List<ProjectDTO> projects;

    private List<ReportDTO> reports;
}
