package Controllers.Web;

import Bienes.Bien;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.CambioDeEstado;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.PersonaJuridica;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Repositories.Donaciones.DonacionesRepository;
import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class DonacionesWebController {

  private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public void verDonacion(Context ctx) {
    DonacionIndependiente donacion = DonacionesRepository.getInstance()
        .obtenerUnoSiExisteSegun("idDominio", ctx.pathParam("id"))
        .filter(d -> !d.estaEliminado())
        .orElseThrow(NotFoundResponse::new);

    Optional<DatosSesion> sesion = ManejadorDeSesion.obtenerSesion(ctx);

    boolean esAdmin = sesion.map(DatosSesion::esAdministrado).orElse(false);
    boolean esDuenio = sesion
        .filter(DatosSesion::esDonante)
        .map(s -> s.idAsociado().equals(donacion.getDonante().getIdDominio()))
        .orElse(false);

    EntidadBeneficiaria asignada = donacion.getAsignado();   // puede ser null
    boolean esReceptora = asignada != null && sesion
        .filter(DatosSesion::esBeneficiario)
        .map(s -> s.idAsociado().equals(asignada.getIdDominio()))
        .orElse(false);

    boolean puedeVerDetalle = esAdmin || esDuenio || esReceptora;
    boolean verDocumento = esAdmin || esDuenio;

    // El público solo ve lo entregado; del resto no confirmamos ni que existe
    if (!puedeVerDetalle && !donacion.estaEn(EstadoDonacion.ENTREGADA)) throw new NotFoundResponse();

    SubCategoria sub = donacion.getSubCategoria();
    Bien bien = donacion.getBienDonado();

    Map<String, Object> modelo = new HashMap<>();
    modelo.put("bien", sub.getNombre());
    modelo.put("cantidad", donacion.getCantidadDeUnidades());
    modelo.put("contenido", bien.getContenido().stripTrailingZeros().toPlainString());
    modelo.put("unidad", donacion.getUnidad().getAbreviatura());
    modelo.put("estado", donacion.getEstado().name());

    // La subcategoría decide el tipo, igual que en el alta del lote
    if (sub.esPerecedera()) {
      modelo.put("vencimiento", ((Perecedero) bien).getVencimiento().format(FECHA));
    } else {
      modelo.put("estadoDeUso", ((NoPerecedero) bien).getEstadoDeUso().name());
    }

    List<String> fotos = donacion.getFotos().stream().map(n -> "/fotos/" + n).toList();
    modelo.put("fotos", fotos);
    modelo.put("tieneFotos", !fotos.isEmpty());
    modelo.put("variasFotos", fotos.size() > 1);

    modelo.put("puedeVerDetalle", puedeVerDetalle);
    modelo.put("verDocumento", verDocumento);

    if (puedeVerDetalle) {
      Map<String, Object> donante = new HashMap<>(modeloDonante(donacion.getDonante()));
      donante.remove("id");
      if (!verDocumento) { donante.remove("documento"); donante.remove("cuit"); }
      modelo.put("donante", donante);

      modelo.put("estaAsignada", asignada != null);
      if (asignada != null) modelo.put("entidad", Map.of("nombre", asignada.getRazonSocial()));

      if (donacion.getJustificacion() != null) modelo.put("justificacion", donacion.getJustificacion());

      // El historial ya viene en orden cronológico (@OrderColumn): alcanza con darlo vuelta
      List<CambioDeEstado> cambios = new ArrayList<>(donacion.getHistorialEstados());
      Collections.reverse(cambios);
      List<Map<String, Object>> historial = cambios.stream().map(c -> {
        Map<String, Object> m = new HashMap<>();
        m.put("estado", c.getEstadoDonacion().name());
        m.put("fecha", c.getFecha());
        if (c.esDeAsignacion() && c.getAsignado() != null) m.put("entidad", c.getAsignado().getRazonSocial());
        return m;
      }).toList();
      modelo.put("historial", historial);
    }

    ctx.render("/donaciones_detalle.hbs", modelo);
  }

  private Map<String, Object> modeloDonante(Donante donante) {
    Map<String, Object> modeloDonante = new HashMap<>();
    modeloDonante.put("id", donante.getIdDominio());
    modeloDonante.put("esHumano", donante.esHumano());

    if (donante.esHumano()) {
      PersonaHumana humano = (PersonaHumana) donante;
      modeloDonante.put("nombre", humano.getNombre());
      modeloDonante.put("apellido", humano.getApellido());
      modeloDonante.put("documento", humano.getDocumento());
    } else {
      PersonaJuridica juridica = (PersonaJuridica) donante;
      modeloDonante.put("razonSocial", juridica.getRazonSocial());
      modeloDonante.put("cuit", juridica.getCuit());
      modeloDonante.put("rubro", juridica.getRubro());
      modeloDonante.put("tipo", juridica.getTipo());
    }
    return modeloDonante;
  }
}
