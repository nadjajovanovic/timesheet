package projekat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.api.api.CountryApi;
import projekat.api.model.CountryDTO;
import projekat.mapper.CountryMapper;
import projekat.services.CountryService;

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
		if (oneCountry.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(CountryMapper.toCountryDTO(oneCountry.get()), HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> insertCountry (@RequestBody CountryDTO country) {
		if (country.getName() == null || country.getName().trim().equals("")
				|| country.getCountryid() != null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var inserted = countryService.create(CountryMapper.toCountry(country));
		return new ResponseEntity(CountryMapper.toCountryDTO(inserted), HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> updateCountry(@RequestBody CountryDTO country) {
		if (country.getName() == null || country.getName().trim().equals("")
				|| country.getCountryid() == null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final var updated = countryService.update(CountryMapper.toCountry(country));
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(updated, HttpStatus.OK);
	}
	
	@CrossOrigin
	@Override
	public ResponseEntity<CountryDTO> deleteCountry (@PathVariable Integer countryid) {
		final var deleted = countryService.delete(countryid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
