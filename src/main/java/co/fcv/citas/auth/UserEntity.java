package co.fcv.citas.auth;

import jakarta.persistence.*;
import java.util.*;

@Entity @Table(name="users")
public class UserEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="first_name", nullable=false) private String firstName;
  @Column(name="last_name", nullable=false) private String lastName;
  @Column(name="document_type", nullable=false) private String documentType;
  @Column(name="document_number", nullable=false, unique=true) private String documentNumber;
  @Column(nullable=false, unique=true) private String email;
  @Column(nullable=false) private String phone;
  @Column(name="password_hash", nullable=false) private String passwordHash;
  @Column(nullable=false) private boolean active = true;
  @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="user_roles", joinColumns=@JoinColumn(name="user_id"))
  @Column(name="role_id") @Convert(converter=RoleIdConverter.class) private Set<Role> roles = new HashSet<>();
  protected UserEntity() {}
  public UserEntity(String firstName,String lastName,String documentType,String documentNumber,String email,String phone,String passwordHash,Role role) {
    this.firstName=firstName; this.lastName=lastName; this.documentType=documentType; this.documentNumber=documentNumber; this.email=email; this.phone=phone; this.passwordHash=passwordHash; this.roles.add(role);
  }
  public Long getId(){return id;} public String getFirstName(){return firstName;} public String getLastName(){return lastName;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public boolean isActive(){return active;} public Set<Role> getRoles(){return roles;}
}
