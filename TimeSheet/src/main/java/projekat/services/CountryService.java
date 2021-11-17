package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
import projekat.exception.InputFieldException;
import projekat.models.Country;
import projekat.repository.CountryRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class CountryService {

    @Autowired
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Collection<Country> getAll() {
        final var countries = countryRepository.findAll();
        return countries;
    }

    public Optional<Country> getOne(Integer id) {
        final var country = countryRepository.findById(id);
        return country;
    }

    public Country create(Country country) {
        if (country.getCountryid() != null) {
            throw new InputFieldException("Id is present in request", ErrorCode.ID_EXISTS);
        }
        final var insertedCountry = countryRepository.save(country);
        return insertedCountry;
    }

    public Country update(Country country){
        if (country.getCountryid() == null) {
            throw new InputFieldException("Id is not present in request", ErrorCode.ID_NOT_FOUND);
        }
        if (!countryRepository.existsById(country.getCountryid())){
            return null;
        }
        final var updatedCountry = countryRepository.save(country);
        return updatedCountry;
    }

    public boolean delete(Integer id) {
        if (!countryRepository.existsById(id)) {
            return false;
        }
        countryRepository.deleteById(id);
        return true;
    }
}
