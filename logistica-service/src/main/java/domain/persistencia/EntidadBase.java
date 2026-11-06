package domain.persistencia;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EntidadBase {

  @Id
  @GeneratedValue
  private Long id;

  public Long getId() {
    return this.id;
  }

}
