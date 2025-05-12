package root.authorithationservicestoryline.webtoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    @Value("${server.port}")
    private String port;
//    @Value("${spring.security.client.validity.duration.minute}")
    private static int VALIDITY_DURATION_MINUTE = 30;

    private static final String SECRET_KEY = "E5B87CD11EC6F7431C78632CE463542DB5944C0210B0B92F5CD8443E934153E469D2905A7B8407DADB485A692B15FF5EB0FA7276E75F1777E30B110E27F15307";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(VALIDITY_DURATION_MINUTE);

    public String generateToken(UserDetails userDetails){
        Map<String,String> claims = new HashMap<>();
        claims.put("iss","http://localhost:"+port);
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();

    }

    private SecretKey generateKey(){
        byte[] decodeKey = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean isTokenValid(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
