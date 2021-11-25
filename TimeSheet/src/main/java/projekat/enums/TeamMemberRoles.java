package projekat.enums;

import org.springframework.security.core.GrantedAuthority;

public enum TeamMemberRoles implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_WORKER;

    @Override
    public String getAuthority() {
        final var roleName = name();
        return roleName;
    }
}
