package Controllers.SolicitudesDeAsignacion;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Controllers.ConOperacionesBasicasGet;
import Controllers.SolicitudesDeAsignacion.Aceptar.AceptarSolicitudRequestDto;
import Controllers.SolicitudesDeAsignacion.EjecutarAlgoritmo.SolicitudDeAsignacionResponseDto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Generales.RepositorioBasico;
import Repositories.SolicitudesDeAsignacion.SolicitudesDeAsignacionRepository;
import TareasProgramadas.CrearSolicitudesDeAsignacion.CrearSolicitudesDeAsignacion;
import io.javalin.http.Context;
import java.util.List;
import java.util.function.Function;

public class SolicitudesDeAsignacionController implements ConOperacionesBasicasGet<SolicitudDeAsignacion, SolicitudDeAsignacionResponseDto> {
  private final CrearSolicitudesDeAsignacion crearSolicitudesDeAsignacion;

  public SolicitudesDeAsignacionController(
      CrearSolicitudesDeAsignacion crearSolicitudesDeAsignacion
  ) {
    this.crearSolicitudesDeAsignacion = crearSolicitudesDeAsignacion;
  }

  public void ejecutarADemanda(Context contexto) {
    List<SolicitudDeAsignacion> solicitudDeAsignacions = crearSolicitudesDeAsignacion.ejecutar();
    contexto.status(201).json(obtenerEnFormatoEnviable(solicitudDeAsignacions));
  }

  public void aceptar(Context contexto) {
    String idSolicitud = contexto.pathParam("idSolicitud");
    AceptarSolicitudRequestDto datos = contexto.bodyAsClass(AceptarSolicitudRequestDto.class);
    EntidadBeneficiaria entidadElegida = BeneficiariosRepository.getInstance().findById(datos.idBeneficiario());
    SolicitudDeAsignacion solicitud = solicitudesRepository().findById(idSolicitud);
    solicitud.aceptar(entidadElegida);
    contexto.status(204);
  }

  @Override
  public Function<SolicitudDeAsignacion, SolicitudDeAsignacionResponseDto> conversorARespuesta() {
    return SolicitudDeAsignacionResponseDto::desde;
  }

  private SolicitudesDeAsignacionRepository solicitudesRepository() {
    return SolicitudesDeAsignacionRepository.getInstance();
  }

  @Override
  public RepositorioBasico<SolicitudDeAsignacion> repositorioPrincipal() {
    return solicitudesRepository();
  }
}
