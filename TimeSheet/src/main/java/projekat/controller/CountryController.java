package projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekat.api.api.CountryApi;
import projekat.api.model.CountryDTO;
import projekat.mapper.CountryMapper;
import projekat.services.CountryService;

import java.util.List;

@RestController
public class CountryController implements CountryApi {

	@Autowired
	private final CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}

	@Override
	public ResponseEntity<List<CountryDTO>> getCountries() {
		final var countries = countryService.getAll()
				.stream()
				.map(CountryMapper::toCountryDTO)
				.toList();
		return new ResponseEntity(countries, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<CountryDTO> getCountry(@PathVariable Integer countryid) {
		final var oneCountry = countryService.getOne(countryid);
		return new ResponseEntity(CountryMapper.toCountryDTO(oneCountry.get()), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> insertCountry (@RequestBody CountryDTO country) {
		final var inserted = countryService.create(CountryMapper.toCountry(country));
		return new ResponseEntity(CountryMapper.toCountryDTO(inserted), HttpStatus.CREATED);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> updateCountry(@RequestBody CountryDTO country) {
		final var updated = countryService.update(CountryMapper.toCountry(country));
		return new ResponseEntity(CountryMapper.toCountryDTO(updated), HttpStatus.OK);
	}

	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> deleteCountry (@PathVariable Integer countryid) {
		countryService.delete(countryid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
