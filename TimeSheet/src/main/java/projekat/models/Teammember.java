package projekat.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import projekat.enums.TeamMemberRoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The persistent class for the teammember database table.
 * 
 */
@Entity
@Getter @Setter @NoArgsConstructor
@NamedQuery(name="Teammember.findAll", query="SELECT t FROM Teammember t")
public class Teammember implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TEAMMEMBER_TEAMMEMBERID_GENERATOR", sequenceName="TEAMMEMBER_SEQ",  allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TEAMMEMBER_TEAMMEMBERID_GENERATOR")
	private Integer teammemberid;

	@Column(nullable = false, unique = true)
	private String email;

	private Double hoursperweek;

	@Enumerated(EnumType.STRING)
	private TeamMemberRoles role;

	private Boolean status;

	private String teammembername;

	@Column(nullable = false, unique = true)
	private String username;

	private String password;

	//bi-directional many-to-one association to Project
	@JsonIgnore
	@OneToMany(mappedBy="teammember")
	private List<Project> projects;

	public Teammember(String userName, String password, ArrayList<Object> objects) {
		this.username = userName;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return status;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status;
	}
}