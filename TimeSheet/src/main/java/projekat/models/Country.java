package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the country database table.
 * 
 */
@Entity
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

	public Country() {
	}

	public Integer getCountryid() {
		return this.countryid;
	}

	public void setCountryid(Integer countryid) {
		this.countryid = countryid;
	}

	public String getCountryname() {
		return this.countryname;
	}

	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}

	public List<Client> getClients() {
		return this.clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public Client addClient(Client client) {
		getClients().add(client);
		client.setCountry(this);

		return client;
	}

	public Client removeClient(Client client) {
		getClients().remove(client);
		client.setCountry(null);

		return client;
	}

}