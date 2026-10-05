package co.fcv.citas.auth;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter
public class RoleIdConverter implements AttributeConverter<Role, Short> {
  public Short convertToDatabaseColumn(Role role){ return role == null ? null : (short)(role.ordinal()+1); }
  public Role convertToEntityAttribute(Short id){ return id == null ? null : Role.values()[id.intValue()-1]; }
}
