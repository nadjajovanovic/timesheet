package projekat.models;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the report database table.
 * 
 */
@Entity
@NamedQuery(name="Report.findAll", query="SELECT r FROM Report r")
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="REPORT_REPORTID_GENERATOR", sequenceName="REPORT_SEQ",  allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_REPORTID_GENERATOR")
	private Integer reportid;

	@Temporal(TemporalType.DATE)
	private Date enddate;

	@Temporal(TemporalType.DATE)
	private Date startdate;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="categoryid")
	private Category category;

	//bi-directional many-to-one association to Client
	@ManyToOne
	@JoinColumn(name="clientid")
	private Client client;

	//bi-directional many-to-one association to Project
	@ManyToOne
	@JoinColumn(name="projectid")
	private Project project;

	//bi-directional many-to-one association to Teammember
	@ManyToOne
	@JoinColumn(name="teammemberid")
	private Teammember teammember;

	public Report() {
	}

	public Integer getReportid() {
		return this.reportid;
	}

	public void setReportid(Integer reportid) {
		this.reportid = reportid;
	}

	public Date getEnddate() {
		return this.enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Date getStartdate() {
		return this.startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Teammember getTeammember() {
		return this.teammember;
	}

	public void setTeammember(Teammember teammember) {
		this.teammember = teammember;
	}

}