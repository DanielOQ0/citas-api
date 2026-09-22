package co.fcv.citas.auth;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.util.*;
@Service public class JwtService {
 private final SecretKey access; private final SecretKey refresh; private final long accessSeconds, refreshSeconds;
 public JwtService(@Value("${app.jwt.access-secret}") String a,@Value("${app.jwt.refresh-secret}") String r,@Value("${app.jwt.access-minutes}") long am,@Value("${app.jwt.refresh-days}") long rd){access=key(a);refresh=key(r);accessSeconds=am*60;refreshSeconds=rd*86400;}
 private SecretKey key(String s){return Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));}
 public String access(UserEntity u){return build(u,access,accessSeconds,"access",UUID.randomUUID().toString());}
 public String refresh(UserEntity u,String jti){return build(u,refresh,refreshSeconds,"refresh",jti);}
 private String build(UserEntity u,SecretKey k,long seconds,String type,String jti){return Jwts.builder().subject(u.getId().toString()).claim("roles",u.getRoles().stream().map(Enum::name).toList()).claim("type",type).id(jti).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+seconds*1000)).signWith(k).compact();}
 public Claims parseAccess(String token){return parse(token,access);} public Claims parseRefresh(String token){return parse(token,refresh);}
 private Claims parse(String token,SecretKey k){return Jwts.parser().verifyWith(k).build().parseSignedClaims(token).getPayload();}
 public long accessSeconds(){return accessSeconds;}
}
