package Persistible;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
abstract public class Persistible {
  @Column(name = "id_dominio", unique = true, nullable = false, updatable = false)
  private String idDominio;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Persistible() {
    this.idDominio = UUID.randomUUID().toString();
  }

  public String getIdDominio() {
     return this.idDominio;
  }

  public boolean esLaMismaEntidad(String id) {
    return this.getIdDominio().equals(id);
  }

  public Long getId() {
    return id;
  }

}
