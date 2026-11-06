package EntidadesBeneficiarias;


import Actualizable.Actualizable;
import Contacto.DatosDeContacto;
import Controllers.EntidadesBenefeciarias.DatosEntidadesBeneficiarias;
import Direccion.Direccion;
import Donaciones.DonacionIndependiente;
import Eliminable.Eliminable;
import ExceptionHandler.NoHayNecesidadCorrespondiente;
import ExceptionHandler.RecursoNoEncontradoException;
import Necesidades.Necesidad;
import Notificable.ConMedioPreferencial;
import Notificable.ConTelefono;
import Persistible.Persistible;
import Validaciones.Validaciones;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EntidadesBeneficiarias")
public class EntidadBeneficiaria extends Persistible implements Actualizable <DatosEntidadesBeneficiarias>, Eliminable, ConMedioPreferencial, ConTelefono {
  @Embedded
  private Direccion direccion;

  @Column(name = "razon_social")
  private String razonSocial;

  @Column(name = "esta_eliminado")
  private boolean estaEliminado = false;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_entidad_beneficiaria_")
  private List<Necesidad> necesidades = new ArrayList<>();

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_datos_de_contacto")
  private DatosDeContacto datosDeContacto;

  protected EntidadBeneficiaria() {

  }

  public EntidadBeneficiaria(String razonSocial, Direccion direccion, DatosDeContacto contacto, List<Necesidad> necesidadesIniciales) {
    this.razonSocial = razonSocial;
    this.direccion = direccion;
    this.datosDeContacto = contacto;
    this.necesidades.addAll(necesidadesIniciales);
  }

  //Getters y Setters Notificable-Contacto

  public List<String> getMails() {
    return this.datosDeContacto.getMailsParaNotificar();
  }

  private void setMails(List<String> nuevosMails) {
    this.datosDeContacto.setMails(nuevosMails);
  }

  public DatosDeContacto getDatosDeContacto() {
    return this.datosDeContacto;
  }

  //Setters y Getters normales
  public String getRazonSocial() {
    return this.razonSocial;
  }

  public Direccion getDireccion() {
    return this.direccion;
  }

  private void setDireccion(Direccion nuevaDireccion) {
    this.direccion = nuevaDireccion;
  }

  private void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  //Eliminable
  public boolean estaEliminado() {
    return this.estaEliminado;
  }

  public void marcarComoEliminado() {
    this.estaEliminado = true;
  }

  //Asignacion de donaciones
  public int cantidadDeDonacionesEnElUltimoTrimestre() {
    var haceTresMeses = LocalDate.now().minusMonths(3);
    return this.getNecesidades().stream()
        .mapToInt(necesidad -> necesidad.cantidadDeDonacionesRecibidasDespuesDe(haceTresMeses))
        .sum();
  }

  public void reicibirDonacionParaDeterminadaNecesidad (DonacionIndependiente unaDonacion) {
    this.buscarNecesidadPara(unaDonacion).recibirBien(unaDonacion);
  }

  public int cantidadDeNecesidadesQueSatisfaceCon(DonacionIndependiente donacion) {
    return this.necesidadesActivasQueSeCorrespondenConLaDonacion(donacion).size();
  }

  public Necesidad buscarNecesidadPara(DonacionIndependiente donacion) {
    return this.necesidadesActivasQueSeCorrespondenConLaDonacion(donacion)
        .stream()
        .findFirst()
        .orElseThrow(NoHayNecesidadCorrespondiente::new);
  }

  public boolean tieneNecesidadPara(DonacionIndependiente donacionIndependiente) {
    return !this.necesidadesActivasQueSeCorrespondenConLaDonacion(donacionIndependiente).isEmpty();
  }


  private List<Necesidad> necesidadesActivasQueSeCorrespondenConLaDonacion(DonacionIndependiente donacion) {
    return this.getNecesidades()
        .stream()
        .filter(unaNecesidad -> unaNecesidad.puedeAceptarA(donacion))
        .toList();
  }


  //Actualizacion
  public void actualizarDesde(DatosEntidadesBeneficiarias datos, boolean parcial) {
    this.setTelefono(Validaciones.actualizar(datos.getTelefonoSeteable(), this.getTelefono(), "El teléfono", parcial));
    this.setMails(Validaciones.actualizar(datos.getMails(), this.getMails(), "Los mails", parcial));
    this.setRazonSocial(Validaciones.actualizar(datos.getRazonSocial(), this.getRazonSocial(), "La razon social", parcial));
    this.setMedioPreferido(Validaciones.actualizar(datos.getTipoDeMedio(), this.getMedioDeNotificacionPreferencial(), "Medio preferencial", parcial));
  }

  //Acceso a Necesidades

  public List<Necesidad> getNecesidades() {
    return List.copyOf(this.necesidades).stream().filter(necesidad -> !necesidad.estaEliminado()).toList();
  }

  public void agregarNecesidad(Necesidad unaNecesidad) {
    this.necesidades.add(unaNecesidad);
  }

  public Necesidad buscarNecesidadPorId(String idNecesidad) {
    return this.getNecesidades().stream()
        .filter(n -> n.getIdDominio().equals(idNecesidad))
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException("La Necesidad cuyo id es: " + idNecesidad + "no existe o, al menos, no pertenece a la entidad beneficiaria en cuestión"));
  }

  public void eliminarNecesidad(String idNecesidad) {
    this.buscarNecesidadPorId(idNecesidad).marcarComoEliminado();
  }

}




