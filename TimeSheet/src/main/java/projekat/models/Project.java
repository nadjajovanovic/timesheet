package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekat.enums.ProjectStatus;

import java.util.List;


/**
 * The persistent class for the project database table.
 * 
 */
@Entity
@Getter @Setter @NoArgsConstructor
@NamedQuery(name="Project.findAll", query="SELECT p FROM Project p")
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PROJECT_PROJECTID_GENERATOR", sequenceName="PROJECT_SEQ",  allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROJECT_PROJECTID_GENERATOR")
	private Integer projectid;

	private String projectdescription;

	private String projectname;

	//bi-directional many-to-one association to Client
	@ManyToOne
	@JoinColumn(name="clientid")
	private Client client;

	//bi-directional many-to-one association to Teammember
	@ManyToOne
	@JoinColumn(name="teammemberid")
	private Teammember teammember;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private ProjectStatus status;

	@JsonIgnore
	@OneToMany(mappedBy="project", fetch = FetchType.LAZY)
	private List<TimeSheetEntry> entries;
}