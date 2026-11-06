package AsignacionDeDonaciones.SolicitudDeAsignacion;


import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ExceptionHandler.DatosInvalidosException;
import Persistible.Persistible;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "SolicitudesDeAsignacion")
public class SolicitudDeAsignacion extends Persistible {

  @ManyToOne
  @JoinColumn(name = "id_donacion_solicitada_para_asignar")
  private DonacionIndependiente donacion;

  @ManyToMany
  @JoinTable(
      name = "Solicitud_De_Asignacion_Entidad_Beneficiaria_Candidata",
      joinColumns = @JoinColumn(name = "id_solicitud_de_asignacion"),
      inverseJoinColumns = @JoinColumn(name = "id_entidad_beneficiaria_candidata")
  )
  private List<EntidadBeneficiaria> candidatas;

  @ManyToOne
  @JoinColumn(name = "id_entidad_beneficiaria_elegida")
  private EntidadBeneficiaria entidadAsignada;

  protected SolicitudDeAsignacion() {

  }
  public SolicitudDeAsignacion(DonacionIndependiente donacion, List<EntidadBeneficiaria> candidatas) {
    this.donacion = donacion;
    this.donacion.enSolicitudDeAsignacion();
    this.candidatas = List.copyOf(candidatas);
  }

  public void aceptar(EntidadBeneficiaria entidadElegida) {
    validarEstado();
    validarEntidadElegible(entidadElegida);
    this.entidadAsignada = entidadElegida;
    donacion.asignarA(entidadElegida);
  }

  private void validarEstado() {
    if (estaAceptada()) {
      throw new DatosInvalidosException("La solicitud ya fue decidida");
    }
  }

  private void validarEntidadElegible(EntidadBeneficiaria entidadElegida) {
    if (!esCandidataValida(entidadElegida)) {
      throw new DatosInvalidosException("La entidad elegida no figura entre las candidatas de esta solicitud");
    }
  }

  public boolean esCandidataValida(EntidadBeneficiaria entidadElegida) {
    return candidatas.contains(entidadElegida);
  }

  public boolean estaAceptada() {
    return entidadAsignada != null;
  }

  public EntidadBeneficiaria getEntidadAsignada() { return entidadAsignada; }
  public DonacionIndependiente getDonacion() { return donacion; }
  public List<EntidadBeneficiaria> getCandidatas() { return candidatas; }
}