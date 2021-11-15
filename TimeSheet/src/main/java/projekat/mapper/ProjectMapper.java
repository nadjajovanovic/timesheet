package projekat.mapper;

import projekat.api.model.ProjectDTO;
import projekat.enums.ProjectStatus;
import projekat.models.Project;

public class ProjectMapper {

    public static ProjectDTO toProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getProjectid());
        dto.setName(project.getProjectname());
        dto.setDescription(project.getProjectdescription());
        dto.setClientId(project.getClientid());
        dto.setTeamMemberId(project.getTeammemberid());
        dto.setStatus(project.getStatus().toString());
        return dto;
    }

    public static Project fromProjectDTO(ProjectDTO dto) {
        Project project = new Project();
        project.setProjectid(dto.getId());
        project.setProjectname(dto.getName());
        project.setProjectdescription(dto.getDescription());
        project.setClientid(dto.getClientId());
        project.setTeammemberid(dto.getTeamMemberId());
        project.setStatus(ProjectStatus.valueOf(dto.getStatus()));
        return project;
    }
}