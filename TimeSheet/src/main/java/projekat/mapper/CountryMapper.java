package projekat.mapper;

import projekat.api.model.CountryDTO;
import projekat.models.Country;

public class CountryMapper {

    public static CountryDTO toCountryDTO (Country country) {
        final var countryDTO = new CountryDTO();
        countryDTO.setCountryid(country.getCountryid());
        countryDTO.setName(country.getCountryname());
        return countryDTO;
    }

    public static Country toCountry (CountryDTO countryDTO) {
        final var country = new Country();
        country.setCountryid(countryDTO.getCountryid());
        country.setCountryname(countryDTO.getName());
        return country;
    }
}
