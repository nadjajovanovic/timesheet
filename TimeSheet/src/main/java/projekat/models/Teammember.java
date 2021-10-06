package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the teammember database table.
 * 
 */
@Entity
@NamedQuery(name="Teammember.findAll", query="SELECT t FROM Teammember t")
public class Teammember implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TEAMMEMBER_TEAMMEMBERID_GENERATOR", sequenceName="TEAMMEMBER_SEQ",  allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TEAMMEMBER_TEAMMEMBERID_GENERATOR")
	private Integer teammemberid;

	private String email;

	private String hoursperweek;

	private String role;

	private String status;

	private String teammembername;

	private String username;

	//bi-directional many-to-one association to Project
	@JsonIgnore
	@OneToMany(mappedBy="teammember")
	private List<Project> projects;

	//bi-directional many-to-one association to Report
	@JsonIgnore
	@OneToMany(mappedBy="teammember")
	private List<Report> reports;

	public Teammember() {
	}

	public Integer getTeammemberid() {
		return this.teammemberid;
	}

	public void setTeammemberid(Integer teammemberid) {
		this.teammemberid = teammemberid;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHoursperweek() {
		return this.hoursperweek;
	}

	public void setHoursperweek(String hoursperweek) {
		this.hoursperweek = hoursperweek;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTeammembername() {
		return this.teammembername;
	}

	public void setTeammembername(String teammembername) {
		this.teammembername = teammembername;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Project addProject(Project project) {
		getProjects().add(project);
		project.setTeammember(this);

		return project;
	}

	public Project removeProject(Project project) {
		getProjects().remove(project);
		project.setTeammember(null);

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
		report.setTeammember(this);

		return report;
	}

	public Report removeReport(Report report) {
		getReports().remove(report);
		report.setTeammember(null);

		return report;
	}

}