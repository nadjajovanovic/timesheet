package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        final var insertedCountry = countryRepository.save(country);
        return insertedCountry;
    }

    public Country update(Country country){
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