package Controllers.Web;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
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
import Repositories.Notificaciones.NotificacionesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import Usuario.Administrador;
import io.javalin.http.Context;

import io.javalin.http.UploadedFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.StringUtils;

public class AdminWebController implements ConPaginacion {

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
    boolean mostrarAlerta = "true".equals(ctx.queryParam("vencida"));

    List<Map<String, Object>> filas = new ArrayList<>();
    for (DonacionIndependiente d : pagina.items()) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", d.getIdDominio());
      fila.put("bien", d.getBienDonado().getSubCategoria().getNombre());
      fila.put("cantidad", d.getCantidadDeSatisfaccion());
      fila.put("unidad", d.getUnidad().getAbreviatura());
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

//private List<Integer> indicesDeItems(Context ctx) {
//  Pattern patron = Pattern.compile("^items\\[(\\d+)\\]\\[");
//  return ctx.formParamMap().keySet().stream()
//      .map(patron::matcher)
//      .filter(Matcher::find)
//      .map(m -> Integer.parseInt(m.group(1)))
//      .distinct()
//      .sorted()
//      .toList();
//}
//
//public void crearLote(Context ctx) {
//  Map<String, Object> model = new HashMap<>();
//  List<String> erroresGlobales = new ArrayList<>();
//  Map<Integer, List<String>> erroresPorItem = new HashMap<>();
//
//  // Lista temporal para retener los datos que el usuario escribió (por si hay que re-renderizar)
//  List<Map<String, Object>> itemsFormulario = new ArrayList<>();
//
//  // 1. Validar Donante
//  String idDonanteStr = ctx.formParam("idDonante");
//  Optional<Donante> potencialDonante = donantesRepository().obtenerSiExistePorIdDominio(idDonanteStr);
//  if (potencialDonante.isEmpty()) {
//    erroresGlobales.add("Debe seleccionar un donante válido.");
//  }
//
//  // 2. Extraer índices
//  List<Integer> indices = indicesDeItems(ctx);
//  if (indices.isEmpty()) {
//    erroresGlobales.add("El lote debe contener al menos un ítem.");
//  }
//
//  // 3. Validar fila por fila en memoria (SIN tocar archivos todavía)
//  for (int i : indices) {
//    String subcatIdStr = ctx.formParam("items[" + i + "][subcategoriaId]");
//    String contenidoStr = ctx.formParam("items[" + i + "][contenido]");
//    String cantidadStr = ctx.formParam("items[" + i + "][cantidad]");
//    String fechaVencimientoStr = ctx.formParam("items[" + i + "][fechaVencimiento]");
//    String estadoUsoStr = ctx.formParam("items[" + i + "][estadoUso]");
//
//    // Guardamos los valores actuales para devolverlos a la vista si hay error
//    Map<String, Object> datosItem = new HashMap<>();
//    datosItem.put("indice", i);
//    datosItem.put("subcategoriaId", subcatIdStr);
//    datosItem.put("contenido", contenidoStr);
//    datosItem.put("cantidad", cantidadStr);
//    datosItem.put("fechaVencimiento", fechaVencimientoStr);
//    datosItem.put("estadoUso", estadoUsoStr);
//    itemsFormulario.add(datosItem);
//
//    List<String> erroresDeEstaFila = new ArrayList<>();
//
//    // Validaciones individuales de la fila
//    if (subcatIdStr == null || subcatIdStr.isBlank()) {
//      erroresDeEstaFila.add("Debe seleccionar una subcategoría.");
//    }
//    if (contenidoStr == null || contenidoStr.isBlank() || Double.parseDouble(contenidoStr) <= 0) {
//      erroresDeEstaFila.add("El contenido debe ser mayor a 0.");
//    }
//    if (cantidadStr == null || cantidadStr.isBlank() || Integer.parseInt(cantidadStr) < 1) {
//      erroresDeEstaFila.add("La cantidad de bultos debe ser al menos 1.");
//    }
//
//    if (subcatIdStr != null && !subcatIdStr.isBlank()) {
//      SubCategoria subcategoria = SubCategoriasRepository.getInstance().obtenerPorIdJPA(Long.parseLong(subcatIdStr));
//      if (subcategoria.esPerecedera()) {
//        if (fechaVencimientoStr == null || fechaVencimientoStr.isBlank()) {
//          erroresDeEstaFila.add("Este producto es perecedero y requiere fecha de vencimiento.");
//        } else {
//          LocalDate fechaVenc = LocalDate.parse(fechaVencimientoStr);
//          if (fechaVenc.isBefore(LocalDate.now())) {
//            erroresDeEstaFila.add("La fecha de vencimiento no puede ser pasada.");
//          }
//        }
//      }
//    }
//
//    if (!erroresDeEstaFila.isEmpty()) {
//      erroresPorItem.put(i, erroresDeEstaFila);
//    }
//  }
//
//  // ==========================================
//  // 4. SI HUBO CUALQUIER ERROR: RE-RENDERIZAR (¡Cero archivos guardados en disco!)
//  // ==========================================
//  if (!erroresGlobales.isEmpty() || !erroresPorItem.isEmpty()) {
//    model.put("erroresGlobales", erroresGlobales);
//    model.put("erroresPorItem", erroresPorItem);
//    model.put("itemsCargados", itemsFormulario); // Para rellenar los inputs
//    model.put("subcategorias", SubCategoriasRepository.getInstance().getInstancias()); // Para los select
//
//    ctx.render("/lotes/nuevo.hbs", model);
//    return; // Cortamos acá, el flujo muere y el disco queda limpio.
//  }
//
//  // ==========================================
//  // 5. SI PASÓ TODO OK: GUARDAR ARCHIVOS Y PERSISTIR
//  // ==========================================
//  LoteBuilder loteBuilder = new LoteBuilder();
//  loteBuilder.conDonante(potencialDonante.get());
//
//  for (int i : indices) {
//    // Acá recién procesamos los archivos con UUID y los guardamos en el volumen Docker
//    List<UploadedFile> fotosValidas = ctx.uploadedFiles("items[" + i + "][fotos]")
//        .stream().filter(f -> f.size() > 0).toList();
//
//    List<String> rutasFotos = new ArrayList<>();
//    for (UploadedFile foto : fotosValidas) {
//      String nombreUnico = UUID.randomUUID().toString() + "_" + foto.filename();
//      // Guardar archivo físicamente en el filesystem...
//      rutasFotos.add(nombreUnico);
//    }
//
//    // Construir el ítem y agregarlo al lote con tu método builder
//    // loteBuilder.agregarItem(...);
//  }
//
//  // loteRepository.guardar(loteBuilder.build());
//  ctx.redirect("/lotes?exito=true");
//}
//
//public void showCrearLote(Context ctx) {
//  ctx.render("/admin/nuevo-lote.hbs");
//}
//
//public void buscarDonanteFragmento(Context ctx) {
//  String legajoODni = ctx.queryParam("q");
//  Optional<Donante> donanteOpt = donantesRepository().buscarPorDocumentoOCuit(legajoODni);
//
//  if (donanteOpt.isPresent()) {
//    Donante donante = donanteOpt.get();
//
//    Map<String, Object> modeloDonante = new HashMap<>();
//    modeloDonante.put("id", donante.getIdDominio());
//    modeloDonante.put("esHumano", donante.esHumano());
//
//    if (donante.esHumano()) {
//      PersonaHumana humano = (PersonaHumana) donante;
//      modeloDonante.put("nombre", humano.getNombre());
//      modeloDonante.put("apellido", humano.getApellido());
//      modeloDonante.put("documento", humano.getDocumento());
//    } else {
//      PersonaJuridica juridica = (PersonaJuridica) donante;
//      modeloDonante.put("razonSocial", juridica.getRazonSocial());
//      modeloDonante.put("cuit", juridica.getCuit());
//      modeloDonante.put("rubro", juridica.getRubro());
//      modeloDonante.put("tipo", juridica.getTipo());
//    }
//    ctx.render("/partials/donante-encontrado.hbs", modeloDonante);
//  } else {
//    ctx.render("/partials/donante-no-encontrado.hbs");
//  }
//}