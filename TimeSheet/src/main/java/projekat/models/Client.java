package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the client database table.
 * 
 */
@Entity
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

	public Client() {
	}

	public Integer getClientid() {
		return this.clientid;
	}

	public void setClientid(Integer clientid) {
		this.clientid = clientid;
	}

	public String getClientaddress() {
		return this.clientaddress;
	}

	public void setClientaddress(String clientaddress) {
		this.clientaddress = clientaddress;
	}

	public String getClientcity() {
		return this.clientcity;
	}

	public void setClientcity(String clientcity) {
		this.clientcity = clientcity;
	}

	public String getClientname() {
		return this.clientname;
	}

	public void setClientname(String clientname) {
		this.clientname = clientname;
	}

	public String getClientzipcode() {
		return this.clientzipcode;
	}

	public void setClientzipcode(String clientzipcode) {
		this.clientzipcode = clientzipcode;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Project addProject(Project project) {
		getProjects().add(project);
		project.setClient(this);

		return project;
	}

	public Project removeProject(Project project) {
		getProjects().remove(project);
		project.setClient(null);

		return project;
	}

	public List<Report> getReports() {
		return this.reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public Report addReport(Report report) {
		getReports().add(report);
		report.setClient(this);

		return report;
	}

	public Report removeReport(Report report) {
		getReports().remove(report);
		report.setClient(null);

		return report;
	}

}