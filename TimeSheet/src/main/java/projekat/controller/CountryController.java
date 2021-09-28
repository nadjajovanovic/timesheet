package projekat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import projekat.models.Country;
import projekat.repository.CountryRepository;

@RestController
public class CountryController {

	private CountryRepository countryRepository;
	
	
	@GetMapping(value = "/country")
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}
	
	@GetMapping("country/{countryId}")
	public Country getCountry(@PathVariable Integer countryId) {
		return countryRepository.getById(countryId);
	}
	
	@GetMapping("country/{countryName}")
	public List<Country> findByCountryName (@PathVariable String countryName) {
		return countryRepository.findByCountryNameContainingIgnoreCase(countryName);
	}
	
	@PostMapping("country")
	public ResponseEntity<Country> insertCountry (@RequestBody Country country) {
		countryRepository.save(country);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("country")
	public ResponseEntity<Country> updateCountry(@RequestBody Country country) {
		if(countryRepository.existsById(country.getCountryid()))
			countryRepository.save(country);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("country/{countryId}")
	public ResponseEntity<Country> deleteCountry (@PathVariable Integer countryId) {
		if (countryRepository.existsById(countryId))
			countryRepository.deleteById(countryId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

}
