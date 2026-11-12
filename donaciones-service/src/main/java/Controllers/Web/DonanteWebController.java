package Controllers.Web;

import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Notificaciones.Notificacion;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class DonanteWebController {

  public void dashboard(Context ctx) {
    Donante donante = getDonante(ctx).get();
    validarDonante(ctx, donante);

    Map<EstadoDonacion, Long> conteos = donacionesRepository().contarDonacionesPorEstadoDeDonante(donante);

    long enDeposito = conteos.getOrDefault(EstadoDonacion.EN_DEPOSITO, 0L);
    long enTraslado = conteos.getOrDefault(EstadoDonacion.EN_TRASLADO, 0L);
    long entregadas = conteos.getOrDefault(EstadoDonacion.ENTREGADA, 0L);
    long asignadas = conteos.getOrDefault(EstadoDonacion.ASIGNACION_REALIZADA, 0L);
    long total = conteos.values().stream().mapToLong(Long::longValue).sum();

    List<Notificacion> recientes = notificacionesRepository().getUltimasDe(
        donante.getDatosDeContacto(), 5
    );

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Mi Panel - Donante");
    model.put("nombreMostrable", ManejadorDeSesion.obtenerNombreMostrable(ctx).get());
    model.put("totalDonaciones", total);
    model.put("enDeposito", enDeposito);
    model.put("enTraslado", enTraslado);
    model.put("entregadas", entregadas);
    model.put("asignadas", asignadas);
    model.put("notificaciones", recientes);

    ctx.render("donante/dashboard.hbs", model);
  }

  public void misDonaciones(Context ctx) {
    Donante donante = getDonante(ctx).get();
    validarDonante(ctx, donante);

    List<DonacionIndependiente> donaciones = donacionesRepository().getDonacionesDeDonante(donante);

    List<Map<String, Object>> filas = getMaps(donaciones);

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Mis Donaciones");
    model.put("donaciones", filas);
    model.put("hayDonaciones", !filas.isEmpty());
    ctx.render("donante/mis-donaciones.hbs", model);
  }

  @NotNull
  private List<Map<String, Object>> getMaps(List<DonacionIndependiente> donaciones) {
    List<Map<String, Object>> filas = new ArrayList<>();
    for (DonacionIndependiente d : donaciones) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", d.getIdDominio());
      fila.put("estado", d.getEstado().name());
      fila.put("colorEstado", colorPara(d.getEstado()));
      fila.put("cantidad", d.getCantidadDeUnidades());
      fila.put("bien", d.getBienDonado().getSubCategoria().getNombre());
      fila.put("fecha", d.getFechaDeDonacion());
      fila.put("entidad", d.getAsignado() != null ? d.getAsignado().getRazonSocial() : "—");
      filas.add(fila);
    }
    return filas;
  }

  public void entidades(Context ctx) {
    validarDonante(ctx, getDonante(ctx).get());
    BeneficiariosRepository beneficiariosRepo = BeneficiariosRepository.getInstance();

    List<EntidadBeneficiaria> entidades = beneficiariosRepo.listarConDatosDeContacto();
    Map<Long, Long> conteosNecesidades = beneficiariosRepo.contarNecesidadesPorEntidad();

    List<Map<String, Object>> filas = new ArrayList<>();
    for (EntidadBeneficiaria e : entidades) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", e.getIdDominio());
      fila.put("razonSocial", e.getRazonSocial());
      fila.put("direccion", e.getDireccion() != null
          ? e.getDireccion().getCalle().getFormatoLegible() : "—");
      fila.put("telefono", e.getTelefono());
      fila.put("email", e.getMails().isEmpty() ? "—" : e.getMails().get(0));

      Long cantidadNecesidades = conteosNecesidades.getOrDefault(e.getId(), 0L);
      fila.put("cantidadNecesidades", cantidadNecesidades);

      filas.add(fila);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Entidades Beneficiarias");
    model.put("sesion", ctx.attribute("sesion"));
    model.put("entidades", filas);
    model.put("hayEntidades", !filas.isEmpty());
    ctx.render("donante/entidades.hbs", model);
  }

  private String colorPara(EstadoDonacion estado) {
    return switch (estado) {
      case ENTREGADA -> "success";
      case EN_TRASLADO -> "info";
      case ASIGNACION_REALIZADA -> "primary";
      case LISTA_PARA_ENTREGAR -> "warning";
      case EN_DEPOSITO -> "dark";
      case ENTREGA_FALLIDA -> "danger";
      case VENCIDA -> "secondary";
      case PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION -> "secondary";
    };
  }

  private Optional<Donante> getDonante(Context context) {
    return DonantesRepository.getInstance().obtenerSiExistePorIdDominio(ManejadorDeSesion.obtenerIdAsoaciado(context).get());
  }

  private void validarDonante(Context context, Donante donante) {
    if(donante == null) {
      context.status(404).redirect("/login");
    }
  }

  private DonacionesRepository donacionesRepository() {
    return DonacionesRepository.getInstance();
  }

  private NotificacionesRepository notificacionesRepository() {
    return NotificacionesRepository.getInstance();
  }
}