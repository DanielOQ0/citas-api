package co.fcv.citas.scheduling.domain;

/** Violación de una regla de negocio. No conoce HTTP: el adaptador REST la traduce (INVALID → 400, CONFLICT → 409). */
public class DomainException extends RuntimeException {
  public enum Kind { INVALID, CONFLICT }

  private final Kind kind;

  public DomainException(Kind kind, String message) {
    super(message);
    this.kind = kind;
  }

  public Kind kind() {
    return kind;
  }

  public static DomainException invalid(String message) {
    return new DomainException(Kind.INVALID, message);
  }

  public static DomainException conflict(String message) {
    return new DomainException(Kind.CONFLICT, message);
  }
}
