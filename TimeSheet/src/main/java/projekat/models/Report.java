package projekat.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter @Setter @NoArgsConstructor
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date enddate;

	private Date startdate;

	private Integer categoryid;

	private Integer clientid;

	private Integer projectid;

	private Integer teammemberid;

}