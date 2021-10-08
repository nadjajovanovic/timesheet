package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * The persistent class for the country database table.
 * 
 */
@Entity
@Getter @Setter @NoArgsConstructor
@NamedQuery(name="Country.findAll", query="SELECT c FROM Country c")
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="COUNTRY_COUNTRYID_GENERATOR", sequenceName="COUNTRY_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COUNTRY_COUNTRYID_GENERATOR")
	private Integer countryid;
	
	private String countryname;

	//bi-directional many-to-one association to Client
	@JsonIgnore
	@OneToMany(mappedBy="country")
	private List<Client> clients;
	
	/*public Country() {
		
	}

	public Integer getCountryid() {
		return countryid;
	}

	public void setCountryid(Integer countryid) {
		this.countryid = countryid;
	}

	public String getCountryname() {
		return countryname;
	}

	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}*/

}