package projekat.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public String extactUsername(String token){
        return exractClaim(token,Claims::getSubject);
    }

    public Date extractExpression(String token){
        return exractClaim(token,Claims::getExpiration);
    }

    public <T> T exractClaim(String token, Function<Claims,T>claimsResolver){
        final var claims = extarctAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extarctAllClaims(String token){
        return Jwts.parser().setSigningKey("Secret_Key").parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        return extractExpression(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object>claims = new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }

    private String createToken(Map<String,Object> claims,String username){
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256,"Secret_Key").compact();
    }
    public boolean validateToken(String token,UserDetails userDetails){
        final var username = extactUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
