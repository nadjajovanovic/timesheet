package projekat.services;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekat.enums.AuthenticationProvider;
import projekat.enums.TeamMemberRoles;
import projekat.exception.BadRequestException;
import projekat.exception.InputFieldException;
import projekat.exception.NotFoundException;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class TeamMemberService implements UserDetailsService {

    @Setter(onMethod_ = {@Autowired})
    private TeamMemberRepository teamMemberRepository;

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;


    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Teammember> getAll() {
        final var teammembers = teamMemberRepository.findAll();
        return teammembers;
    }

    @PreAuthorize("hasRole('WORKER') or hasRole('ADMIN')")
    public Optional<Teammember> getOne(Integer id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var oneTeamMember = teamMemberRepository.findById(id);
        return oneTeamMember;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Teammember insert(Teammember teammember) {
        if (teammember.getTeammemberid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }
        teammember.setPassword(passwordEncoder.encode(teammember.getPassword()));
        teammember.setProvider(AuthenticationProvider.LOCAL);
        final var inserted = teamMemberRepository.save(teammember);
        return inserted;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public Teammember update(Teammember teammember) {
        final var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var loggedInUser = (Teammember)user;
        if(loggedInUser.getRole().equals(TeamMemberRoles.ROLE_WORKER) && !Objects.equals(teammember.getUsername(), loggedInUser.getUsername()))
            throw new BadRequestException("You are not allowed to change this entry", HttpStatus.FORBIDDEN);
        if (teammember.getTeammemberid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.BAD_REQUEST);
        }
        if (!teamMemberRepository.existsById(teammember.getTeammemberid())) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", teammember.getTeammemberid()), HttpStatus.NOT_FOUND);
        }
        final var updated = teamMemberRepository.save(teammember);
        return updated;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(Integer id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        teamMemberRepository.deleteById(id);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = teamMemberRepository.findByUsername(username);
        if (user.isEmpty())
            throw new NotFoundException(String.format("Team member with username %s does not exist in database", username), HttpStatus.NOT_FOUND);
        return user.get();
    }

    public UserDetails processOAuthPostLogin(String username) {
        final var existUser = teamMemberRepository.findByUsername(username);

        if (existUser.isEmpty()) {
            final var newUser = new Teammember();
            newUser.setUsername(username);
            newUser.setStatus(true);
            newUser.setHoursperweek(35.5);
            newUser.setRole(TeamMemberRoles.ROLE_WORKER);
            newUser.setProvider(AuthenticationProvider.GOOGLE);
            teamMemberRepository.save(newUser);
            return newUser;
        }
        return existUser.get();
    }

}
