package projekat.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import projekat.services.JwtUtilService;
import projekat.services.TeamMemberService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private JwtUtilService jwtUtilService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("Authentication Success");
        final var oauthUser = (DefaultOidcUser) authentication.getPrincipal();
        final var userDetails = teamMemberService.processOAuthPostLogin(oauthUser.getEmail());
        final var jwt = jwtUtilService.generateToken(userDetails);
        response.getWriter().println(jwt);
        response.setStatus(HttpStatus.OK.value());
    }
}
