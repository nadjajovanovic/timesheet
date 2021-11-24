package projekat.enums;

import org.springframework.security.core.GrantedAuthority;

public enum TeamMemberRoles implements GrantedAuthority {
    ADMIN,
    WORKER;

    @Override
    public String getAuthority() {
        return name();
    }

}
