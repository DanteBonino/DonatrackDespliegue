package Notificaciones;

import Contacto.DatosDeContacto;
import MedioDeNotificacion.TipoDeMedio;
import Persistible.Persistible;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Notificaciones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_notificacion")
abstract public class Notificacion extends Persistible {
  public static final int MAXIMO_REINTENTOS = 3;

  @Column(name = "mensaje")
  private String mensaje;

  @ManyToOne
  @JoinColumn(name = "id_datos_de_contacto")
  private DatosDeContacto contacto;

  @Column(name = "veces_rebotada")
  private int vecesRebotada = 0;

  @Column(name = "fue_entregada")
  private boolean fueEntregada = false;

  protected Notificacion() {}

  public Notificacion(String mensaje, DatosDeContacto contacto) {
    this.mensaje = mensaje;
    this.contacto = contacto;
  }

  public void serRebotada(){
    this.vecesRebotada ++;
  }

  public Boolean esEnviable() {
    return this.noRebotoLaCantidadMaximaDeVeces() && !fueEntregada;
  }

  private Boolean noRebotoLaCantidadMaximaDeVeces() {
    return vecesRebotada < MAXIMO_REINTENTOS;
  }

  public void pasarAEntregada() {
    this.fueEntregada = true;
  }

  public DatosDeContacto getReceptor() {
    return this.contacto;
  }

  public String getMensaje() {
    return this.mensaje;
  }

  abstract public TipoDeMedio getMedioDeNotificacion();

  abstract public boolean esEnviablePorElMedio(TipoDeMedio unMedioDeNotificacion);
}
