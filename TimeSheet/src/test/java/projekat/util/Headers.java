package projekat.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import projekat.enums.TeamMemberRoles;
import projekat.models.Teammember;
import projekat.repository.TeamMemberRepository;

import java.net.http.HttpHeaders;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Headers implements MockMvcBuilderCustomizer {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private final String secretKey = "Secret key";
    private final String username = "adminTest";


    @Override
    public void customize(ConfigurableMockMvcBuilder<?> builder) {
        RequestBuilder apiKeyRequestBuilder = MockMvcRequestBuilders.multipart("/*")
                .header("Authorization", createAuth());
        builder.defaultRequest(apiKeyRequestBuilder);
    }

    public String createAuth( ){
        Map<String, Object> claims = new HashMap<>();
        final var jwt =  Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(Long.MAX_VALUE))
                .signWith(SignatureAlgorithm.HS256,secretKey).compact();
        return "Bearer "+jwt;
    }

    public  Teammember saveTeamMember() {
        final var teammember = new Teammember();
        teammember.setTeammembername("name");
        teammember.setPassword("$2a$10$oUvS02vbxyTUe3J5ZlGV8e4lM2Rnkdfcvcc9cXAtQYCbxq3rfgiKe");
        teammember.setUsername("adminTest");
        teammember.setEmail("test@example.com");
        teammember.setStatus(true);
        teammember.setRole(TeamMemberRoles.ROLE_ADMIN);
        teammember.setHoursperweek(2.3);
        return teamMemberRepository.saveAndFlush(teammember);
    }
}