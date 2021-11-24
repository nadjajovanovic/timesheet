package projekat.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor @Getter @Setter
public class TimeSheetEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="ENTRY_ENTRYID_GENERATOR", sequenceName="ENTRY_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ENTRY_ENTRYID_GENERATOR")
    private Integer entryId;

    @ManyToOne
    @JoinColumn(name="teammember")
    private Teammember teammember;

    @Column(name = "teammemberid", nullable = false)
    private Integer teammemberid;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date entryDate;

    //bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name="client")
    private Client client;

    @ManyToOne
    @JoinColumn(name="project")
    private Project project;

    @ManyToOne
    @JoinColumn(name="category")
    private Category category;

    @Column(name = "clientid", nullable = false)
    private Integer clientid;

    @Column(name = "projectid", nullable = false)
    private Integer projectid;

    @Column(name = "categoryid", nullable = false)
    private Integer categoryid;

    private String description;

    @Column(nullable = false)
    private Double time;

    private Double overtime;
}
