package projekat.mapper;

import projekat.api.model.TeamMemberDTO;
import projekat.models.Teammember;

import java.math.BigDecimal;

public class TeamMemberMapper {
    public static TeamMemberDTO toTeamMemberDTO(Teammember teamMember) {
        final var teammemberDTO = new TeamMemberDTO();
        teammemberDTO.setId(teamMember.getTeammemberid());
        teammemberDTO.setName(teamMember.getTeammembername());
        teammemberDTO.setUsername(teamMember.getUsername());
        teammemberDTO.setEmail(teamMember.getEmail());
        teammemberDTO.setStatus(teamMember.getStatus());
        teammemberDTO.setHoursPerWeek(BigDecimal.valueOf(teamMember.getHoursperweek()));
        return teammemberDTO;
    }

    public static Teammember toTeamMember(TeamMemberDTO teamMemberDTO) {
        final var teammember = new Teammember();
        teammember.setTeammemberid(teamMemberDTO.getId());
        teammember.setTeammembername(teamMemberDTO.getName());
        teammember.setUsername(teamMemberDTO.getUsername());
        teammember.setEmail(teamMemberDTO.getUsername());
        teammember.setStatus(teamMemberDTO.getStatus());
        teammember.setHoursperweek(teamMemberDTO.getHoursPerWeek().doubleValue());
        return teammember;
    }
}
