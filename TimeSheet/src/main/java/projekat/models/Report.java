package projekat.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date enddate;

	private Date startdate;

	private Integer categoryid;

	private Integer clientid;

	private Integer projectid;

	private Integer teammemberid;

}