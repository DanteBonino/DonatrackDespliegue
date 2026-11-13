package Controllers.Web;

import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.PersonaJuridica;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Necesidades.Necesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Generales.paginacion.Pagina;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import io.javalin.http.Context;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeneficiariaWebController implements ConPaginacion {

  public void dashboard(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);

    Map<EstadoDonacion, Long> conteos = donacionesRepository().contarDonacionesPorEstadoDe(entidad);


    long enTraslado = conteos.getOrDefault(EstadoDonacion.EN_TRASLADO, 0L);
    long entregadas = conteos.getOrDefault(EstadoDonacion.ENTREGADA, 0L);
    long asignadas = conteos.getOrDefault(EstadoDonacion.ASIGNACION_REALIZADA, 0L);
    long listasParaEntregar = conteos.getOrDefault(EstadoDonacion.LISTA_PARA_ENTREGAR, 0L);
    long pendientes = asignadas + listasParaEntregar;

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Mi Panel");
    model.put("entidad", true);
    model.put("razonSocial", entidad.getRazonSocial());
    model.put("necesidades", entidad.getNecesidades().size());
    model.put("asignadas", asignadas);
    model.put("enTraslado", enTraslado);
    model.put("entregadas", entregadas);
    model.put("pendientes", pendientes);

    ctx.render("beneficiaria/dashboard.hbs", model);
  }

  public void necesidades(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);

    List<Necesidad> todas = entidad.getNecesidades();

    Pagina<Necesidad> pagina = Pagina.de(todas, numeroDePagina(ctx), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);

    List<Map<String, Object>> filas = pagina.items().stream()
        .map(n -> {
          Map<String, Object> m = new HashMap<>();
          m.put("id", n.getIdDominio());
          m.put("unidad", n.getSubCategoriaRequerida().getUnidad());
          m.put("subCategoria", n.getSubCategoriaRequerida() != null ? n.getSubCategoriaRequerida().getNombre() : "-");
          m.put("cantidadRequerida", n.cantidadRequerida() != null ? n.cantidadRequerida().toString() : "-");
          m.put("tipo", n.getTipo() != null ? n.getTipo().name() : "-");
          m.put("satisfecha", n.estaSatisfecha());
          return m;
        })
        .toList();

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Necesidades");
    model.put("necesidades", filas);
    model.put("total", todas.size());
    model.put("paginas", pagina.aModelo());
    ctx.render("beneficiaria/necesidades.hbs", model);
  }

  public void donacionesAsignadas(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);
    Pagina<DonacionIndependiente> pagina = DonacionesRepository.getInstance().getDonacionesDe(entidad, numeroDePagina(ctx), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);
    List<Map<String, Object>> donaciones = pagina.items().stream()
        .map(d -> {
          Map<String, Object> m = new HashMap<>();
          m.put("id", d.getIdDominio());
          m.put("estado", d.getEstado().name());
          m.put("cantidad", d.getCantidadDeSatisfaccion());
          m.put("unidad", d.getUnidad().getAbreviatura());
          m.put("subCategoria", d.getSubCategoria() != null ? d.getSubCategoria().getNombre() : "-");
          m.put("fecha", d.getFechaDeDonacion());
          m.put("donante", nombreDe(d.getDonante()));
          return m;
        })
        .toList();

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Donaciones Asignadas");
    model.put("donaciones", donaciones);
    model.put("hayDonaciones", !donaciones.isEmpty());
    model.put("paginas", pagina.aModelo());
    ctx.render("beneficiaria/donacionesAsignadas.hbs", model);
  }


  private String nombreDe(Donante donante) {
    if (donante == null) return "-";
    if (donante instanceof PersonaHumana ph) return ph.getNombre() + " " + ph.getApellido();
    if (donante instanceof PersonaJuridica pj) return pj.getRazonSocial();
    return "-";
  }

  private Optional<EntidadBeneficiaria> getBeneficiario(Context context) {
    return BeneficiariosRepository.getInstance().obtenerSiExistePorIdDominio(ManejadorDeSesion.obtenerIdAsoaciado(context).get());
  }

  private void validarBeneficiario(Context context, EntidadBeneficiaria beneficiario) {
    if(beneficiario == null) {
      context.status(404).redirect("/login");
    }
  }

  private DonacionesRepository donacionesRepository() {
    return DonacionesRepository.getInstance();
  }
}