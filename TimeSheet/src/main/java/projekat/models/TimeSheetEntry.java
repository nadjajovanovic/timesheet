package projekat.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor @Getter @Setter
public class TimeSheetEntry {

    @Id
    @SequenceGenerator(name="ENTRY_ENTRYID_GENERATOR", sequenceName="ENTRY_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ENTRY_ENTRYID_GENERATOR")
    private Integer entryId;

    // TODO add user when authentication is done

    @Temporal(TemporalType.DATE)
    private Date entryDate;

    //bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name="clientid")
    private Client client;

    @ManyToOne
    @JoinColumn(name="projectid")
    private Project project;

    @ManyToOne
    @JoinColumn(name="categoryid")
    private Category category;

    private String description;

    private Double time;

    private Double overtime;
}
