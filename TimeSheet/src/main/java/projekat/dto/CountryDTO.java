package projekat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CountryDTO {

    private Integer countryid;

    private String countryname;

    private List<ClientDTO> clients;
}
