package co.fcv.citas.auth;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter
public class RoleIdConverter implements AttributeConverter<Role, Long> {
  public Long convertToDatabaseColumn(Role role){ return role == null ? null : (long)(role.ordinal()+1); }
  public Role convertToEntityAttribute(Long id){ return id == null ? null : Role.values()[id.intValue()-1]; }
}
