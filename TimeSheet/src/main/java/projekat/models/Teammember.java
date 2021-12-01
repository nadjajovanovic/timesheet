package projekat.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import projekat.enums.AuthenticationProvider;
import projekat.enums.TeamMemberRoles;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

	private String email;

	private Double hoursperweek;

	@Enumerated(EnumType.STRING)
	private TeamMemberRoles role;

	private Boolean status;

	private String teammembername;

	private String username;

	private String password;

	@Enumerated(EnumType.STRING)
	private AuthenticationProvider provider;

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
		final var authorityList = Arrays.asList(role);
		return authorityList;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
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