package Controllers.Web;

import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.PersonaJuridica;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.Necesidad;
import Necesidades.TipoNecesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Generales.paginacion.Pagina;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import UsuarioFinal.Rol;
import io.javalin.http.Context;

import io.javalin.validation.Validation;
import io.javalin.validation.ValidationError;
import io.javalin.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeneficiariaWebController implements ConPaginacion, ConValidacionDeFormulario {

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
          BigDecimal recibida = n.cantidadRecibida();
          BigDecimal requerida = n.cantidadRequerida();
          double porcentajeCalc = requerida.signum() > 0 ? recibida.divide(requerida, 2, RoundingMode.HALF_UP).doubleValue() * 100 : 0.0;
          double porcentaje = Math.min(porcentajeCalc, 100.0);
          m.put("id", n.getIdDominio());
          m.put("unidad", n.getSubCategoriaRequerida().getUnidad().getAbreviatura());
          m.put("subCategoria", n.getSubCategoriaRequerida() != null ? n.getSubCategoriaRequerida().getNombre() : "-");
          m.put("cantidadRequerida", requerida);
          m.put("cantidadRecibida", recibida);
          m.put("porcentaje", porcentaje);
          m.put("faltante", requerida.subtract(recibida));
          m.put("tipo", n.getTipo() != null ? n.getTipo().name() : "-");
          m.put("satisfecha", n.estaSatisfecha());
          return m;
        })
        .toList();

    Optional<String> mensajeCreada = Optional.ofNullable(ctx.queryParam("creada"))
        .flatMap(id -> todas.stream()
            .filter(n -> n.getIdDominio().equals(id))
            .findFirst()
            .map(n -> "Se registró exitosamente la necesidad de " +
                (n.getSubCategoriaRequerida() != null ? n.getSubCategoriaRequerida().getNombre() : "bien")));
    boolean fueEliminada = ctx.queryParam("eliminada") != null;
    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Necesidades");
    model.put("necesidades", filas);
    model.put("total", todas.size());
    model.put("paginas", pagina.aModelo());
    mensajeCreada.ifPresent(msg -> model.put("mensajeCreada", msg));
    if (fueEliminada) {
      model.put("mensajeEliminada", "La necesidad fue eliminada exitosamente.");
    }
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
          m.put("fechaAsignacion", d.getFechaDeUltimaAsignacion().get());
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

  public void showCrearNecesidad(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);

    Map<String, String> errores = getErrores(ctx);
    Map<String, String> valores = getValores(ctx);

    List<SubCategoria> subCategorias = subCategoriasRepository().getInstancias();

    List<Map<String, Object>> subcategoriasDto = subCategorias.stream()
        .map(sc -> {
          Map<String, Object> map = new HashMap<>();
          map.put("id", sc.getIdDominio());
          map.put("nombre", sc.getNombre());
          map.put("unidad", sc.getUnidad().getAbreviatura());
          map.put("admiteDecimales", sc.getUnidad().admiteDecimales());
          map.put("esPerecedera", sc.esPerecedera());
          return map;
        })
        .toList();

    Map<String, Object> model = new HashMap<>();
    model.put("subcategorias", subcategoriasDto);
    model.put("errores", errores);
    model.put("valores", valores);
    ctx.render("nueva-necesidad.hbs", model);
  }

  public void showGuiaNecesidades(Context context) {
    EntidadBeneficiaria entidad = getBeneficiario(context).get();
    validarBeneficiario(context, entidad);
    context.render("beneficiaria/guia-necesidades.hbs");
  }

  public void crearNecesidad(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);

    Optional<SubCategoria> subcategoriaOpt = Optional.ofNullable(ctx.formParam("subcategoriaId"))
        .flatMap(id -> subCategoriasRepository().obtenerUnoSiExisteSegun("idDominio", id));

    Validator<String> subcategoriaValidator = ctx.formParamAsClass("subcategoriaId", String.class)
        .required()
        .check(id -> subcategoriaOpt.isPresent(), "Elegí uno de los bienes de la lista");

    Validator<BigDecimal> cantidadValidator = ctx.formParamAsClass("cantidad", BigDecimal.class)
        .required()
        .check(cantidad -> cantidad != null && cantidad.signum() > 0, "La cantidad tiene que ser mayor a 0")
        .check(cantidad -> subcategoriaOpt
                .map(sc -> sc.getUnidad().admiteDecimales() || esEntera(cantidad))
                .orElse(true),
            "Para este bien la cantidad tiene que ser un número entero");

    Validator<TipoNecesidad> tipoValidator = ctx.formParamAsClass("tipo", TipoNecesidad.class).required();

    Validator<TipoCondicion> tipoCondicionValidator = ctx.formParamAsClass("condicion", TipoCondicion.class)
        .required()
        .check(condicion -> subcategoriaOpt
                .map(sc -> !sc.esPerecedera() || condicion == TipoCondicion.BASICA)
                .orElse(true),
            "Los productos perecederos se aceptan solo con condición básica (vigencia)");

    boolean requierePeriodo = tipoValidator.errors().isEmpty() && tipoValidator.get().requierePeriodo();

    List<Validator<?>> validadoresAProcesar = new ArrayList<>(List.of(
        subcategoriaValidator, cantidadValidator, tipoCondicionValidator, tipoValidator
    ));

    Validator<FrecuenciaPeriodo> frecuenciaValidator = null;
    if (requierePeriodo) {
      frecuenciaValidator = ctx.formParamAsClass("periodo", FrecuenciaPeriodo.class).required();
      validadoresAProcesar.add(frecuenciaValidator);
    }

    Map<String, List<ValidationError<Object>>> errors = Validation.collectErrors(
        validadoresAProcesar.toArray(new Validator[0])
    );

    if (!errors.isEmpty()) {
      Map<String, Object> valores = Map.of(
          "subcategoriaId", getValue(ctx, "subcategoriaId"),
          "cantidad", getValue(ctx, "cantidad"),
          "condicion", getValue(ctx, "condicion"),
          "tipo", getValue(ctx, "tipo"),
          "periodo", getValue(ctx, "periodo") != null ? getValue(ctx, "periodo") : ""
      );
      volver(ctx, "/necesidades/nueva", obtenerErroresConDetalle(errors), valores);
      return;
    }

    SubCategoria subCategoria = subcategoriaOpt.get();
    BigDecimal cantidad = cantidadValidator.get();
    TipoCondicion tipoCondicion = tipoCondicionValidator.get();

    CondicionDeAceptacion condicion = tipoCondicion.crearInstancia(subCategoria);

    Period periodo = (frecuenciaValidator != null) ? frecuenciaValidator.get().getPeriodJava() : null;

    Necesidad nuevaNecesidad = tipoValidator.get().crearInstancia(condicion, cantidad, periodo);
    entidad.agregarNecesidad(nuevaNecesidad);

    ctx.redirect("/necesidades?creada=" + nuevaNecesidad.getIdDominio());
  }

  public void eliminarNecesidad(Context ctx) {
    EntidadBeneficiaria entidad = getBeneficiario(ctx).get();
    validarBeneficiario(ctx, entidad);

    String idNecesidad = ctx.pathParam("id");

    entidad.eliminarNecesidad(idNecesidad);

    ctx.redirect("/necesidades?eliminada=true");
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

  private SubCategoriasRepository subCategoriasRepository() {
    return SubCategoriasRepository.getInstance();
  }

  private boolean esEntera(BigDecimal cantidad) {
    return cantidad.stripTrailingZeros().scale() <= 0;
  }

  public String getFinalDeClave() {
    return "Necesidad";
  }
}