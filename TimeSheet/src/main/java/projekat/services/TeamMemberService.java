package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
import projekat.exception.NotFoundException;
import projekat.exception.BadRequestException;
import projekat.exception.InputFieldException;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class TeamMemberService implements UserDetailsService {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public Collection<Teammember> getAll() {
        final var teammembers = teamMemberRepository.findAll();
        return teammembers;
    }

    public Optional<Teammember> getOne(Integer id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", id), HttpStatus.NOT_FOUND);
        }
        final var oneTeamMember = teamMemberRepository.findById(id);
        return oneTeamMember;
    }

    public Teammember insert(Teammember teammember) {
        if (teammember.getTeammemberid() != null) {
            throw new InputFieldException("Id is present in request", HttpStatus.BAD_REQUEST);
        }
        final var inserted = teamMemberRepository.save(teammember);
        return inserted;
    }

    public Teammember update(Teammember teammember) {
        if (teammember.getTeammemberid() == null) {
            throw new InputFieldException("Id is not present in request", HttpStatus.BAD_REQUEST);
        }
        if (!teamMemberRepository.existsById(teammember.getTeammemberid())) {
            throw new NotFoundException(String.format("Team member with id %d does not exist in database", teammember.getTeammemberid()), HttpStatus.NOT_FOUND);
        }
        final var updated = teamMemberRepository.save(teammember);
        return updated;
    }

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
}
