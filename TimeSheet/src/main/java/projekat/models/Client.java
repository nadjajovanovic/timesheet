package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * The persistent class for the client database table.
 * 
 */
@Entity
@Getter @Setter @NoArgsConstructor
@NamedQuery(name="Client.findAll", query="SELECT c FROM Client c")
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CLIENT_CLIENTID_GENERATOR", sequenceName="CLIENT_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CLIENT_CLIENTID_GENERATOR")
	private Integer clientid;
	
	private String clientaddress;
	
	private String clientcity;
	
	private String clientname;
	
	private String clientzipcode;

	//bi-directional many-to-one association to Country
	@ManyToOne
	@JoinColumn(name="countryid")
	private Country country;

	//bi-directional many-to-one association to Project
	@JsonIgnore
	@OneToMany(mappedBy="client")
	private List<Project> projects;

	//bi-directional many-to-one association to Report
	@JsonIgnore
	@OneToMany(mappedBy="client")
	private List<Report> reports;

}