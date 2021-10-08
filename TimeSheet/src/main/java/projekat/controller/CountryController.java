package projekat.controller;

import java.util.List;

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
import projekat.repository.CountryRepository;

@RestController
public class CountryController {

	@Autowired
	private CountryRepository countryRepository;
	
	public CountryController(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}
	
	@GetMapping(value = "/country")
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}
	
	@GetMapping("country/{countryid}")
	public Country getCountry(@PathVariable Integer countryid) {
		return countryRepository.getById(countryid);
	}
	
	@CrossOrigin
	@PostMapping("country")
	public ResponseEntity<Country> insertCountry (@RequestBody Country country) {
		countryRepository.save(country);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("country")
	public ResponseEntity<Country> updateCountry(@RequestBody Country country) {
		if(countryRepository.existsById(country.getCountryid()))
			countryRepository.save(country);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("country/{countryid}")
	public ResponseEntity<Country> deleteCountry (@PathVariable Integer countryid) {
		if (countryRepository.existsById(countryid))
			countryRepository.deleteById(countryid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

}
