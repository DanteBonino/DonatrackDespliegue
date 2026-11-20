package Controllers.Web;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import Donaciones.Item;
import Donaciones.Lote.Lote;
import Donaciones.Lote.LoteBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.PersonaJuridica;
import EstadoDonacion.EstadoDonacion;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Generales.paginacion.Pagina;
import Repositories.Lotes.LotesRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import Usuario.Administrador;
import io.javalin.http.Context;

import io.javalin.http.UploadedFile;
import io.javalin.validation.Validation;
import io.javalin.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import utils.FotoStorage;
import utils.Fotos;
import utils.StringUtils;

public class AdminWebController implements ConPaginacion, ConValidacionDeFormulario {
  private static final int MAX_FOTOS_POR_ITEM = 5;
  private static final long MAX_BYTES_FOTO = 5L * 1024 * 1024;
  private static final Set<String> TIPOS_FOTO = Set.of("image/jpeg", "image/png", "image/webp");

  public AdminWebController() {
  }

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
    Pagina<DonacionIndependiente> pagina = donacionesRepository()
        .getDonacionesEnDepositoConDetalles(numeroDePagina(ctx), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);
    Map<Long, Long> loteDe = donacionesRepository().getLotesDe(pagina.items());
    boolean mostrarAlerta = "true".equals(ctx.queryParam("vencida"));

    List<Map<String, Object>> filas = new ArrayList<>();
    for (DonacionIndependiente d : pagina.items()) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", d.getIdDominio());
      fila.put("lote", loteDe.get(d.getId()));
      fila.put("bien", d.getBienDonado().getSubCategoria().getNombre());
      fila.put("cantidad", d.getCantidadDeSatisfaccion());
      fila.put("unidad", d.getUnidad().getAbreviatura());
      fila.put("fecha", d.getFechaDeDonacion());
      fila.put("donante", d.getDonante().getCaracteristicaDeDiferenciacion());
      fila.put("bienVencido", !d.sigueEnVigencia());
      fila.put("esPerecedero", d.getSubCategoria().esPerecedera());
      filas.add(fila);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Gestión de Depósito");
    model.put("sesion", ctx.attribute("sesion"));
    model.put("donaciones", filas);
    model.put("hayDonaciones", !filas.isEmpty());
    model.put("paginas", pagina.aModelo());
    model.put("vencidaExito", mostrarAlerta);
    ctx.render("admin/deposito.hbs", model);
  }

  public void vencerDonacion(Context ctx) {
    DonacionIndependiente donacion = DonacionesRepository.getInstance()
        .findById(ctx.pathParam("id"));
    donacion.vencer();
    ctx.redirect("/donaciones/en-deposito?vencida=true");
  }

  public void showBuscarDonante(Context ctx) {
    validarAdministrador(ctx);
    ctx.render("admin/buscar-donante.hbs");
  }

  public void buscarDonante(Context ctx) {
    validarAdministrador(ctx);
    String q = StringUtils.sacarGuionesPuntosYEspacios(Optional.ofNullable(ctx.formParam("q")).orElse(""));
    Optional<Donante> donante = q.isBlank() ? Optional.empty()
        : donantesRepository().buscarPorDocumentoOCuit(q);

    if (donante.isPresent()) {
      ctx.redirect("/lotes/nuevo/items?donante=" + donante.get().getIdDominio());
    } else {
      ctx.render("/admin/buscar-donante.hbs",
          Map.of("error", "No se encontró ningún donante con ese DNI o CUIT", "q", q));
    }
  }

  public void showCrearLote(Context ctx) {
    Optional<Donante> donanteOpt = donanteDe(ctx.queryParam("donante"));
    if (donanteOpt.isEmpty()) { ctx.redirect("/lotes/nuevo/buscador-de-donantes"); return; }
    Donante donante = donanteOpt.get();

    Map<String, String> errores = getErrores(ctx);
    Map<String, String> valores = getValores(ctx);

    if (!donante.getIdDominio().equals(valores.get("donante"))) {
      errores = Map.of();
      valores = Map.of();
    }

    List<String> generales = Optional.ofNullable(errores.get("general")).map(List::of).orElse(List.of());
    renderPaso2(ctx, donante, filasDe(valores, errores), generales);
  }

  private List<Map<String, Object>> filasDe(Map<String, String> valores, Map<String, String> errores) {
    Pattern patron = Pattern.compile("^items\\[(\\d+)]\\[(\\w+)]$");
    SortedSet<Integer> indices = new TreeSet<>();
    Stream.concat(valores.keySet().stream(), errores.keySet().stream()).forEach(clave -> {
      Matcher m = patron.matcher(clave);
      if (m.matches()) indices.add(Integer.parseInt(m.group(1)));
    });

    if (indices.isEmpty()) return List.of(filaVacia("0"));

    List<Map<String, Object>> filas = new ArrayList<>();
    for (int i : indices) {
      Map<String, Object> fila = filaVacia(String.valueOf(i));
      valoresDe(fila).clear();
      String prefijo = "items[" + i + "][";
      valores.forEach((k, v) -> { if (k.startsWith(prefijo)) valoresDe(fila).put(campoDe(k), v); });
      errores.forEach((k, v) -> { if (k.startsWith(prefijo)) erroresDe(fila).put(campoDe(k), v); });
      filas.add(fila);
    }
    return filas;
  }

  private Optional<Donante> donanteDe(String idDominio) {
    return Optional.ofNullable(idDominio)
        .flatMap(id -> donantesRepository().obtenerSiExistePorIdDominio(id));
  }

  private void renderPaso2(Context ctx, Donante donante,
                           List<Map<String, Object>> filas, List<String> generales) {
    int siguiente = filas.stream()
        .mapToInt(f -> Integer.parseInt((String) f.get("indice")))
        .max().orElse(-1) + 1;

    List<Map<String, Object>> subcategorias = subCategoriasRepository().getInstancias().stream()
        .map(sc -> {
          Map<String, Object> m = new HashMap<>();
          m.put("idDominio", sc.getIdDominio());
          m.put("nombre", sc.getNombre());
          m.put("perecedera", sc.esPerecedera());
          m.put("admiteDecimales", sc.getUnidad().admiteDecimales());
          m.put("unidad", sc.getUnidad().getAbreviatura());
          return m;
        }).toList();

    LinkedHashMap<Object, Object> EstadoUso;
    List<Map<String, String>> estadosUso = Arrays.stream(EstadoDeUso.values())
        .map(e -> Map.of("valor", e.name(), "etiqueta", e.name()))
        .toList();

    Map<String, Object> modelo = new HashMap<>();
    modelo.put("donante", modeloDonante(donante));
    modelo.put("donanteId", donante.getIdDominio());
    modelo.put("filas", filas);
    modelo.put("plantilla", filaVacia("__INDICE__"));
    modelo.put("siguienteIndice", siguiente);
    modelo.put("subcategorias", subcategorias);
    modelo.put("estadosUso", estadosUso);
    modelo.put("errores", generales);
    ctx.render("/admin/nuevo-lote-items.hbs", modelo);
  }

  public void crearLote(Context ctx) {
    Optional<Donante> donanteOpt = donanteDe(ctx.formParam("donante"));
    if (donanteOpt.isEmpty()) { ctx.redirect("/lotes/nuevo/buscador-de-donantes"); return; }
    Donante donante = donanteOpt.get();
    String urlPaso2 = "/lotes/nuevo/items?donante=" + donante.getIdDominio();

    Map<String, String> errores = new HashMap<>();
    Map<String, Object> valores = new HashMap<>();
    valores.put("donante", donante.getIdDominio());

    List<Integer> indices = indicesDeItems(ctx);
    if (indices.isEmpty()) errores.put("general", "El lote debe tener al menos un ítem");

    List<ItemSolicitado> solicitados = new ArrayList<>();
    for (int i : indices) {
      leerItem(ctx, i, errores, valores).ifPresent(solicitados::add);
    }

    if (!errores.isEmpty()) {
      volver(ctx, urlPaso2, errores, valores);   // redirect: F5 ya no reenvía el formulario
      return;
    }

    List<String> fotosEscritas = new ArrayList<>();
    try {
      List<Item> items = new ArrayList<>();
      for (ItemSolicitado s : solicitados) {
        List<String> nombres = Fotos.storage().guardar(s.fotos());
        fotosEscritas.addAll(nombres);
        items.add(s.aItem(nombres));
      }
      Lote lote = new LoteBuilder().conDonante(donante).conItems(items).build();
      lotesRepository().registrar(lote);
      ctx.redirect("/donaciones/en-deposito?loteCreado=" + lote.getIdDominio());
    } catch (RuntimeException e) {
      Fotos.storage().eliminar(fotosEscritas);
      throw e;
    }
  }

  private Optional<ItemSolicitado> leerItem(Context ctx, int i,
                                            Map<String, String> errores, Map<String, Object> valores) {
    String p = "items[" + i + "]";
    Set<String> erroresAntes = new HashSet<>(errores.keySet());

    for (String campo : List.of("subcategoriaId", "contenido", "cantidad", "fechaVencimiento", "estadoUso")) {
      String clave = p + "[" + campo + "]";
      String v = ctx.formParam(clave);
      if (v != null) valores.put(clave, v);
    }

    Optional<SubCategoria> subOpt = Optional.ofNullable(ctx.formParam(p + "[subcategoriaId]"))
        .flatMap(id -> subCategoriasRepository().obtenerUnoSiExisteSegun("idDominio", id));

    Validator<String> subcategoria = ctx.formParamAsClass(p + "[subcategoriaId]", String.class)
        .required()
        .check(id -> subOpt.isPresent(), "Elegí uno de los bienes de la lista");

    Validator<BigDecimal> contenido = ctx.formParamAsClass(p + "[contenido]", BigDecimal.class)
        .required()
        .check(c -> c.signum() > 0, "El contenido tiene que ser mayor a 0")
        .check(c -> subOpt.map(sc -> sc.getUnidad().admiteDecimales() || esEntera(c)).orElse(true),
            "Para este bien el contenido tiene que ser un número entero");

    Validator<Integer> cantidad = ctx.formParamAsClass(p + "[cantidad]", Integer.class)
        .required()
        .check(c -> c >= 1, "La cantidad tiene que ser 1 o más");

    List<Validator<?>> validadores = new ArrayList<>(List.of(subcategoria, contenido, cantidad));
    Validator<LocalDate> vencimiento = null;
    Validator<EstadoDeUso> estadoDeUso = null;

    if (subOpt.isPresent()) {   // el servidor decide qué se valida, no el formulario
      if (subOpt.get().esPerecedera()) {
        vencimiento = ctx.formParamAsClass(p + "[fechaVencimiento]", LocalDate.class)
            .required()
            .check(f -> !f.isBefore(LocalDate.now()), "El vencimiento no puede ser una fecha pasada");
        validadores.add(vencimiento);
      } else {
        estadoDeUso = ctx.formParamAsClass(p + "[estadoUso]", EstadoDeUso.class).required();
        validadores.add(estadoDeUso);
      }
    }

    errores.putAll(obtenerErroresConDetalle(
        Validation.collectErrors(validadores.toArray(new Validator[0]))));

    List<UploadedFile> fotos = ctx.uploadedFiles(p + "[fotos]").stream()
        .filter(f -> f.size() > 0).toList();
    if (fotos.size() > MAX_FOTOS_POR_ITEM) {
      errores.put(p + "[fotos]", "Máximo " + MAX_FOTOS_POR_ITEM + " fotos por ítem");
    } else if (fotos.stream().anyMatch(f -> !TIPOS_FOTO.contains(f.contentType()) || f.size() > MAX_BYTES_FOTO)) {
      errores.put(p + "[fotos]", "Solo JPG, PNG o WebP de hasta 5 MB");
    }

    boolean huboErroresNuevos = !erroresAntes.containsAll(errores.keySet());
    if (huboErroresNuevos) return Optional.empty();

    return Optional.of(new ItemSolicitado(subOpt.get(), contenido.get(), cantidad.get(),
        vencimiento != null ? vencimiento.get() : null,
        estadoDeUso != null ? estadoDeUso.get() : null, fotos));
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

  private SubCategoriasRepository subCategoriasRepository() {
    return SubCategoriasRepository.getInstance();
  }

  private LotesRepository lotesRepository() {
    return LotesRepository.getInstance();
  }

  private Map<String, Object> filaVacia(String indice) {
    Map<String, String> valores = new HashMap<>();
    valores.put("cantidad", "1");
    Map<String, Object> fila = new HashMap<>();
    fila.put("indice", indice);
    fila.put("valores", valores);
    fila.put("errores", new HashMap<String, String>());
    return fila;
  }

  private List<Integer> indicesDeItems(Context ctx) {
    Pattern patron = Pattern.compile("^items\\[(\\d+)]\\[");
    return ctx.formParamMap().keySet().stream()
        .map(patron::matcher)
        .filter(Matcher::find)
        .map(m -> Integer.parseInt(m.group(1)))
        .distinct().sorted().toList();
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> valoresDe(Map<String, Object> fila) {
    return (Map<String, String>) fila.get("valores");
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> erroresDe(Map<String, Object> fila) {
    return (Map<String, String>) fila.get("errores");
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

  public record ItemSolicitado(SubCategoria subcategoria, BigDecimal contenido, int cantidad,
                               LocalDate vencimiento, EstadoDeUso estadoDeUso, List<UploadedFile> fotos) {

    public Item aItem(List<String> nombresDeFotos) {
      Bien bien = subcategoria.esPerecedera()
          ? new Perecedero(subcategoria, contenido, vencimiento)
          : new NoPerecedero(subcategoria, contenido, estadoDeUso);
      return new Item(bien, cantidad, nombresDeFotos);
    }
  }

  @Override
  public String getFinalDeClave() { return "Lote"; }

  private boolean esEntera(BigDecimal cantidad) {
    return cantidad.stripTrailingZeros().scale() <= 0;
  }

  private String campoDe(String clave) {
    int inicio = clave.lastIndexOf('[');
    if (inicio < 0 || !clave.endsWith("]")) return clave;
    return clave.substring(inicio + 1, clave.length() - 1);
  }
}
