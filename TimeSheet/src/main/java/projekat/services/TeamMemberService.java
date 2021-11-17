package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.enums.ErrorCode;
import projekat.exception.BadRequestException;
import projekat.exception.InputFieldException;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class TeamMemberService {

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
        final var oneTeamMember = teamMemberRepository.findById(id);
        return oneTeamMember;
    }

    public Teammember insert(Teammember teammember) {
        if (teammember.getTeammemberid() != null) {
            throw new InputFieldException("Id is present in request", ErrorCode.ID_EXISTS);
        }
        final var inserted = teamMemberRepository.save(teammember);
        return inserted;
    }

    public Teammember update(Teammember teammember) {
        if (teammember.getTeammemberid() == null) {
            throw new InputFieldException("Id is not present in request", ErrorCode.ID_NOT_FOUND);
        }
        if (!teamMemberRepository.existsById(teammember.getTeammemberid()))
            throw new BadRequestException(String.format("No exist object with %d id in DB",teammember.getTeammemberid()), ErrorCode.NOT_FOUND);
        final var updated = teamMemberRepository.save(teammember);
        return updated;
    }

    public boolean delete(Integer id) {
        if (!teamMemberRepository.existsById(id))
            throw new BadRequestException(String.format("No exist object with %d id in DB",id),ErrorCode.NOT_FOUND);
        teamMemberRepository.deleteById(id);
        return true;
    }
}
