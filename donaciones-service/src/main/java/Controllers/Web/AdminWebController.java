package Controllers.Web;

import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EstadoDonacion.EstadoDonacion;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import Usuario.Administrador;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminWebController {

  public void dashboard(Context ctx) {
    validarAdministrador(ctx);

    Map<EstadoDonacion, Long> conteos = donacionesRepository().contarDonacionesPorEstadoGlobal();


    long enDeposito          = conteos.getOrDefault(EstadoDonacion.EN_DEPOSITO, 0L);
    long pendientesAsignacion = conteos.getOrDefault(EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION, 0L);
    long vencidas            = conteos.getOrDefault(EstadoDonacion.VENCIDA, 0L);
    long entregadas          = conteos.getOrDefault(EstadoDonacion.ENTREGADA, 0L);
    long enTraslado          = conteos.getOrDefault(EstadoDonacion.EN_TRASLADO, 0L);
    long entregaFallida      = conteos.getOrDefault(EstadoDonacion.ENTREGA_FALLIDA, 0L);
    long totalDonaciones     = conteos.values().stream().mapToLong(Long::longValue).sum();

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Panel de Administración");
    model.put("sesion", ctx.attribute("sesion"));
    model.put("totalDonaciones", totalDonaciones);
    model.put("enDeposito", enDeposito);
    model.put("pendientesAsignacion", pendientesAsignacion);
    model.put("vencidas", vencidas);
    model.put("entregadas", entregadas);
    model.put("enTraslado", enTraslado);
    model.put("entregaFallida", entregaFallida);
    model.put("totalDonantes", donantesRepository().contarDonantesActivos());
    model.put("totalBeneficiarias", beneficiariosRepository().contarBeneficiariasActivas());

    ctx.render("admin/dashboard.hbs", model);
  }

  public void deposito(Context ctx) {
    validarAdministrador(ctx);
    List<DonacionIndependiente> enDeposito = donacionesRepository().getDonacionesEnDepositoConDetalles();
    boolean mostrarAlerta = "true".equals(ctx.queryParam("vencida"));
    List<Map<String, Object>> filas = new ArrayList<>();
    for (DonacionIndependiente d : enDeposito) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", d.getIdDominio());
      fila.put("bien", d.getBienDonado().getSubCategoria().getNombre());
      fila.put("cantidad", d.getCantidadDeUnidades());
      fila.put("fecha", d.getFechaDeDonacion());
      fila.put("donante", d.getDonante().getCaracteristicaDeDiferenciacion());
      fila.put("bienVencido", !d.sigueEnVigencia());
      filas.add(fila);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Gestión de Depósito");
    model.put("sesion", ctx.attribute("sesion"));
    model.put("donaciones", filas);
    model.put("hayDonaciones", !filas.isEmpty());
    model.put("vencidaExito", mostrarAlerta);
    ctx.render("admin/deposito.hbs", model);
  }

  public void vencerDonacion(Context ctx) {
    DonacionIndependiente donacion = DonacionesRepository.getInstance()
        .findById(ctx.pathParam("id"));
    donacion.vencer();
    ctx.redirect("/donaciones/en-deposito?vencida=true");
  }

  private Optional<Administrador> getAdministrador(Context context) {
    return administradoresRepository().obtenerSiExistePorIdDominio(ManejadorDeSesion.obtenerIdAsoaciado(context).get());
  }

  private void validarAdministrador(Context context) {
    if(getAdministrador(context).isEmpty()) {
      context.status(404).redirect("/login");
    }
  }

  private AdministradoresRepository administradoresRepository() {
    return AdministradoresRepository.getInstance();
  }

  private DonacionesRepository donacionesRepository() {
    return DonacionesRepository.getInstance();
  }

  private DonantesRepository donantesRepository() {
    return DonantesRepository.getInstance();
  }

  private BeneficiariosRepository beneficiariosRepository() {
    return BeneficiariosRepository.getInstance();
  }

}