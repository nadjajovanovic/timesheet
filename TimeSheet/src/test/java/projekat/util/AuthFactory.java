package projekat.util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class AuthFactory {


    public String createAuth(String userName, String password ){
        Map<String, Object> claims = new HashMap<>();
        final var jwt =  Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(Long.MAX_VALUE))
                .signWith(SignatureAlgorithm.HS256,"Secret key").compact();
        return "Bearer "+jwt;
    }
}
