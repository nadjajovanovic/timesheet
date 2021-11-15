package projekat.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Country;
import projekat.services.CountryService;

@RestController
public class CountryController {

	@Autowired
	private final CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}

	@GetMapping(value = "/country")
	public ResponseEntity<Collection<Country>> getCountries() {
		final var countries = countryService.getAll();
		return new ResponseEntity<>(countries, HttpStatus.OK);
	}
	
	@GetMapping("country/{countryid}")
	public ResponseEntity<Country> getCountry(@PathVariable Integer countryid) {
		final var countryOptional = countryService.getOne(countryid);
		if (countryOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(countryOptional.get(), HttpStatus.OK);
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
		return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("country/{countryid}")
	public ResponseEntity<Country> deleteCountry (@PathVariable Integer countryid) {
		final var deleted = countryService.delete(countryid);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
