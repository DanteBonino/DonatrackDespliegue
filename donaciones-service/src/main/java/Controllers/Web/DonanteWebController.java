package Controllers.Web;

import Bienes.Categoria.Categoria;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Notificaciones.Notificacion;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Generales.paginacion.Pagina;
import Repositories.Notificaciones.NotificacionesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class DonanteWebController implements ConPaginacion {
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

    FiltroDonaciones filtro = filtroDe(ctx);
    Pagina<DonacionIndependiente> pagina = donacionesRepository()
        .getDonacionesDeDonante(donante, filtro, numeroDePagina(ctx), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);

    List<Map<String, Object>> filas = getMaps(pagina.items());

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Mis Donaciones");
    model.put("donaciones", filas);
    model.put("hayDonaciones", !filas.isEmpty());
    model.put("hayFiltros", filtro.hayFiltros());
    model.put("filtros", opcionesDeFiltro(filtro));
    model.put("paginas", pagina.aModelo(filtro.comoQuery()));
    ctx.render("donante/mis-donaciones.hbs", model);
  }

  private FiltroDonaciones filtroDe(Context ctx) {
    return new FiltroDonaciones(
        parsearEstado(ctx.queryParam("estado")),
        parsearId(ctx.queryParam("categoria")),
        parsearId(ctx.queryParam("subcategoria")));
  }

  private EstadoDonacion parsearEstado(String valor) {
    try {
      return valor == null || valor.isBlank() ? null : EstadoDonacion.valueOf(valor);
    } catch (IllegalArgumentException e) {   // valor inventado en la URL: se ignora
      return null;
    }
  }

  private Long parsearId(String valor) {
    try {
      return valor == null || valor.isBlank() ? null : Long.valueOf(valor);
    } catch (NumberFormatException e) {
      return null;
    }
  }



  @NotNull
  private List<Map<String, Object>> getMaps(List<DonacionIndependiente> donaciones) {
    List<Map<String, Object>> filas = new ArrayList<>();
    for (DonacionIndependiente d : donaciones) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", d.getIdDominio());
      fila.put("estado", d.getEstado().name());
      fila.put("cantidad", d.getCantidadDeSatisfaccion());
      fila.put("unidad", d.getUnidad().getAbreviatura());
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

    Pagina<EntidadBeneficiaria> pagina = beneficiariosRepo.listarPagina(numeroDePagina(ctx), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);
    Map<Long, Long> conteosNecesidades = beneficiariosRepo.contarNecesidadesPorEntidad();

    List<Map<String, Object>> filas = new ArrayList<>();
    for (EntidadBeneficiaria e : pagina.items()) {
      Map<String, Object> fila = new HashMap<>();
      fila.put("id", e.getIdDominio());
      fila.put("razonSocial", e.getRazonSocial());
      fila.put("direccion", e.getDireccion() != null
          ? e.getDireccion().getCalle().getFormatoLegible() : "—");
      fila.put("cantidadNecesidades", conteosNecesidades.getOrDefault(e.getId(), 0L));
      filas.add(fila);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Entidades Beneficiarias");
    model.put("sesion", ctx.attribute("sesion"));
    model.put("entidades", filas);
    model.put("hayEntidades", !filas.isEmpty());
    model.put("paginas", pagina.aModelo());
    ctx.render("donante/entidades.hbs", model);
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

  private Map<String, Object> opcionesDeFiltro(FiltroDonaciones filtro) {
    List<Map<String, Object>> estados = Arrays.stream(EstadoDonacion.values())
        .map(e -> Map.<String, Object>of(
            "valor", e.name(),
            "etiqueta", EstilosEstado.etiquetaDe(e.name()),
            "seleccionado", e == filtro.estado()))
        .toList();

    Map<Categoria, List<SubCategoria>> porCategoria = SubCategoriasRepository.getInstance().getInstancias().stream()
        .collect(Collectors.groupingBy(SubCategoria::getCategoria, LinkedHashMap::new, Collectors.toList()));

    List<Map<String, Object>> categorias = porCategoria.entrySet().stream()
        .map(en -> Map.<String, Object>of(
            "id", en.getKey().getId(),
            "nombre", en.getKey().getNombre(),
            "seleccionada", Objects.equals(en.getKey().getId(), filtro.categoriaId()),
            "subcategorias", en.getValue().stream()
                .map(s -> Map.<String, Object>of(
                    "id", s.getId(),
                    "nombre", s.getNombre(),
                    "seleccionada", Objects.equals(s.getId(), filtro.subCategoriaId())))
                .toList()))
        .toList();

    return Map.of("estados", estados, "categorias", categorias);
  }
}