package Controllers.Donanciones;

import Controllers.ConOperacionesBasicasGet;
import Controllers.Donanciones.Estados.AsignacionRealizada.DonacionAsignacionRealizadaResponseDto;
import Controllers.Donanciones.Estados.AsignacionRealizada.DonacionAsignadaRequestDto;
import Controllers.Donanciones.Estados.ListaParaEntregar.DonacionListaParaEntregarResponseDto;
import Controllers.Donanciones.Estados.EnTraslado.EnTrasladoRequestDto;
import Controllers.Donanciones.Estados.EntregaExitosa.EntregaExitosaRequestDto;
import Controllers.Donanciones.Estados.EntregaFallida.EntregaFallidaRequestDto;
import Controllers.Donanciones.Get.DonacionResponseDto;
import Controllers.Donanciones.HistorialDeCambiosDeEstado.CambioDeEstadoResponseDto;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Generales.RepositorioBasico;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import Usuario.Administrador;
import io.javalin.http.Context;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class DonacionesController implements ConOperacionesBasicasGet<DonacionIndependiente, DonacionResponseDto> {

  @Override
  public RepositorioBasico<DonacionIndependiente> repositorioPrincipal() {
    return donacionesRepository();
  }

  @Override
  public Function<DonacionIndependiente, DonacionResponseDto> conversorARespuesta() {
    return DonacionResponseDto::desde;
  }

  public void delete(@NotNull Context context) {
    donacionesRepository().eliminarInstancia(context.pathParam("id"));
    context.status(204);
  }

  public void iniciarTraslado (Context context) {
    EnTrasladoRequestDto request = context.bodyValidator(EnTrasladoRequestDto.class)
        .check(obj -> obj.idEntrega() != null, "El ID de la entrega es obligatorio")
        .get();

    this.getDonacionIndependiente(context).iniciarTraslado(request.idEntrega());
    context.status(204);
  }

  public void marcarComoEntregada(Context context) {
    EntregaExitosaRequestDto dto = context.bodyValidator(EntregaExitosaRequestDto.class)
        .check(obj -> obj.comprobanteDeEntrega() != null, "El comprobante de entrega es obligatorio")
        .get();

    ComprobanteDeEntrega comprobanteDeEntrega = dto.comprobanteDeEntrega();
    this.getDonacionIndependiente(context).marcarComoEntregada(comprobanteDeEntrega);

    context.status(204);
  }

  public void marcarComoFallida(Context context) {
    EntregaFallidaRequestDto dto = context.bodyValidator(EntregaFallidaRequestDto.class)
        .check(obj -> obj.justificacion() != null, "La justificacion de la falla es obligatoria")
        .get();
    DonacionIndependiente donacionIndependiente = this.getDonacionIndependiente(context);
    donacionIndependiente.marcarComoFallida(dto.justificacion());
    context.status(204);
  }

  public void almacenarEnDeposito(Context context) {
    this.getDonacionIndependiente(context).almacenarEnDeposito();
    context.status(204);
  }

  //Este no está porque se supone que hace por la ruta de los algoritmos
  public void asignarEntidad(Context contexto) {
    DonacionAsignadaRequestDto dto = contexto.bodyAsClass(DonacionAsignadaRequestDto.class);
    this.getDonacionIndependiente(contexto).asignarA(this.getEntidadBeneficiaria(dto.idBeneficiario()));
    contexto.status(204);
  }

  private DonacionIndependiente getDonacionIndependiente(Context context) {
    return this.donacionesRepository().findById(context.pathParam("id"));
  }

  private EntidadBeneficiaria getEntidadBeneficiaria(String id) {
    return BeneficiariosRepository.getInstance().findById(id);
  }

  public void alistarParaEntregar(Context contexto) {
    this.getDonacionIndependiente(contexto).alistarParaEntregar();
    contexto.status(204);
  }

  public void vencer(Context contexto) {
    validarAdministrador(contexto);
    this.getDonacionIndependiente(contexto).vencer();
    contexto.status(204);
  }


  public void getListasParaEntregar(Context ctx) {
    responderConDonaciones(ctx,
        donacionesRepository().getListasParaEntregar(),
        DonacionListaParaEntregarResponseDto::desde);
  }

  public void getAsignadas(Context ctx) {
    responderConDonaciones(ctx,
        donacionesRepository().getAsignadas(),
        DonacionAsignacionRealizadaResponseDto::desde); // Usas el DTO estándar
  }

  public void getPendientesDeRevision(Context ctx) {
    responderConDonaciones(ctx,
        donacionesRepository().getPendientesDeRevision(),
        DonacionResponseDto::desde);
  }

  //Se podría parametrizar el DTO

  private <T> void responderConDonaciones(Context ctx, List<DonacionIndependiente> donaciones, Function<DonacionIndependiente, T> mapper) {
    List<T> respuesta = donaciones.stream()
        .map(mapper)
        .toList();
    ctx.json(respuesta);
  }

  public void getHistorialDeCambiosDeEstado(Context contexto) {
    List<CambioDeEstadoResponseDto> respuesta = this.getDonacionIndependiente(contexto)
        .getHistorialEstados()
        .stream()
        .map(CambioDeEstadoResponseDto::desde)
        .toList();
    contexto.json(respuesta);
  }

  private DonantesRepository donantesRepository() {
    return DonantesRepository.getInstance();
  }
  private DonacionesRepository donacionesRepository() {
    return DonacionesRepository.getInstance();
  }
  private AdministradoresRepository administradoresRepository() {
    return AdministradoresRepository.getInstance();
  }

  private Optional<Administrador> getAdministrador(Context context) {
    return administradoresRepository().obtenerSiExistePorIdDominio(ManejadorDeSesion.obtenerIdAsoaciado(context).get());
  }

  private void validarAdministrador(Context context) {
    if(getAdministrador(context).isEmpty()) {
      context.status(404).redirect("/login");
    }
  }
}
