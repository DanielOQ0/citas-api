package co.fcv.citas.auth;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="refresh_tokens") public class RefreshTokenEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne(optional=false) @JoinColumn(name="user_id") UserEntity user;
 @Column(name="token_id",nullable=false,unique=true) String tokenId; @Column(name="expires_at",nullable=false) Instant expiresAt; @Column(name="revoked_at") Instant revokedAt;
 protected RefreshTokenEntity(){} public RefreshTokenEntity(UserEntity u,String t,Instant e){user=u;tokenId=t;expiresAt=e;} public UserEntity getUser(){return user;} public Instant getExpiresAt(){return expiresAt;} public Instant getRevokedAt(){return revokedAt;} public void revoke(){revokedAt=Instant.now();}
}
