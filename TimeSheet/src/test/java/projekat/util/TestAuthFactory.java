package projekat.util;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import projekat.services.TeamMemberService;

@Component
public class TestAuthFactory {

    @Autowired
    private TeamMemberService teamMemberService;

    @SneakyThrows
    public void loginUser(String userName) {
        final var userDetails = teamMemberService.loadUserByUsername(userName);
        final var token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
