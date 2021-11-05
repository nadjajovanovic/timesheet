package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekat.enums.TeamMemberRoles;

import java.util.List;


/**
 * The persistent class for the teammember database table.
 * 
 */
@Entity
@Getter @Setter @NoArgsConstructor
@NamedQuery(name="Teammember.findAll", query="SELECT t FROM Teammember t")
public class Teammember implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TEAMMEMBER_TEAMMEMBERID_GENERATOR", sequenceName="TEAMMEMBER_SEQ",  allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TEAMMEMBER_TEAMMEMBERID_GENERATOR")
	private Integer teammemberid;

	private String email;

	private Double hoursperweek;

	private TeamMemberRoles role;

	private Boolean status;

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
}