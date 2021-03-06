package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import projekat.exception.InputFieldException;
import projekat.exception.NotFoundException;
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
        if (!countryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Country with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var country = countryRepository.findById(id);
        return country;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Country create(Country country) {
        if (country.getCountryid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.NOT_FOUND);
        }
        final var insertedCountry = countryRepository.save(country);
        return insertedCountry;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Country update(Country country){
        if (country.getCountryid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.NOT_FOUND);
        }
        if (!countryRepository.existsById(country.getCountryid())){
            throw new NotFoundException(String.format("Country with id %d does not exist in database", country.getCountryid()), HttpStatus.NOT_FOUND);
        }
        final var updatedCountry = countryRepository.save(country);
        return updatedCountry;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Country with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        countryRepository.deleteById(id);
        return true;
    }
}
