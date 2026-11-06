package Controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Bienes.Categoria.Categoria;
import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.Bien.TipoBien;
import Controllers.Lote.ItemDtoParaCreacionDeLote;
import Controllers.Lote.LoteDto;
import Donantes.Donante;
import Injector.Injector;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ServidorDonaciones;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeDatos;
import utils.FabricaDeSubCategorias;
import utils.FechasUtils;

@ExtendWith(MockitoExtension.class)
public class LoteControllerTest implements ConLecturaDeRespuestas, ConLimpiezaDeBaseDeDatos {
  private final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  private Javalin app;
  private Donante donante;
  private DonantesRepository donantesRepository;
  private Map<String, Object> moldeParaJson;
  private SubCategoria remera;
  private SubCategoria arroz;
  private DonacionesRepository donacionesRepository;
  @Mock
  private Injector inyectorDeDependencias;

  @BeforeEach
  void setUp() {
    remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
    arroz = FabricaDeSubCategorias.getSubCategoriaDeAlimento("arroz");
    donante = FabricaDeDatos.unDonanteDePrueba();
    donantesRepository = DonantesRepository.getInstance();
    donacionesRepository = DonacionesRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
    withTransaction(() -> {
      donantesRepository.registrar(donante);
      registrarSubCategoria(remera);
      registrarSubCategoria(arroz);
    });
    moldeParaJson = new HashMap<>();
  }

  @Nested
  class SinLotesPersistidos {
    @Test
    void pedirTodasDevuelveUnaListaVaciaY200ComoStatusCode() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(leerRespuestasMultiples(respuestaHttp)).isEmpty();
      });
    }

    @Test
    void crearUnLoteDevuelve400SiNoSeEnviaElIdDelDonante() {
      moldeParaJson.put("items", List.of());
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve400SiNoSeEnviaUnaListaDeItems() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve404SiElIdDelDonanteNoCoincideConNingunDonantePersistido() {
      moldeParaJson.put("donanteId", "11223344");
      moldeParaJson.put("items", List.of());
      chequearStatusCodeDeRequest(404, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve400SiSeEnviaUnaListaVaciaDeItems() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of());
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve400SiSeEnviaUnaListaConItemDeTipoPerecederoYUnaSubCategoriaQueNoEsPerecedera() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotos(
              BigDecimal.valueOf(1),
              null,
              10,
              TipoBien.PERECEDERO,
              LocalDate.now(),
              remera.getNombre()
          )
      ));
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve400SiSeEnviaUnaListaConItemDeTipoNoPerecederoYUnaSubCategoriaQueEsPerecedera() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotos(
              BigDecimal.valueOf(1),
              EstadoDeUso.NUEVO,
              10,
              TipoBien.NOPERECEDERO,
              LocalDate.now(),
              arroz.getNombre()
          )
      ));
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }

    @Test
    void crearUnLoteDevuelve400SiSeEnviaUnaListaConItemQueNoTieneContenido() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotos(
              null,
              EstadoDeUso.NUEVO,
              10,
              TipoBien.NOPERECEDERO,
              null,
              remera.getNombre()
          )
      ));
      chequearStatusCodeDeRequest(400, moldeParaJson);
    }


    @Test
    void crearUnLoteDevuelve201SiSeCreaSinProblemas() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotosDeRemeraNueva(10)
      ));
      chequearStatusCodeDeRequest(201, moldeParaJson);
    }

    @Test
    void seAgrupanLosItemsParaCrearLasDonaciones() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotosDeRemeraNueva(10),
          getItemSinFotosDeRemeraNueva(15)
      ));
      JavalinTest.test(app, (server, client) -> {
        LoteDto loteCreado =  leerIndividual(client.post(pathBase(), mapper.writeValueAsString(moldeParaJson)));
        assertEquals(1, loteCreado.getDonaciones().size());
        assertEquals(25, loteCreado.getDonaciones().get(0).getItem().cantidadUnidades());
      });
    }

    @Test
    void noSeAgrupanItemsDeDistintoContenido() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotoDePaqueteDeArrozDe500GramosVenceEnUnMes(10),
          getItemSinFotoDePaqueteDeArrozDe250GramosVenceEnUnMes(4)
          ));
      JavalinTest.test(app, (server, client) -> {
        LoteDto loteCreado =  leerIndividual(client.post(pathBase(), mapper.writeValueAsString(moldeParaJson)));
        assertEquals(2, loteCreado.getDonaciones().size());
        assertTrue(loteCreado.getDonaciones().stream().anyMatch(donacion -> donacion.getItem().cantidadUnidades() == 10));
        assertTrue(loteCreado.getDonaciones().stream().anyMatch(donacion -> donacion.getItem().cantidadUnidades() == 4));
      });
    }

    @Test
    void sePersistenLasDonacionesRealmente() {
      moldeParaJson.put("donanteId", donante.getIdDominio());
      moldeParaJson.put("items", List.of(
          getItemSinFotosDeRemeraNueva(10),
          getItemSinFotosDeRemeraNueva(15)
      ));
      JavalinTest.test(app, (server, client) -> {
        LoteDto loteCreado =  leerIndividual(client.post(pathBase(), mapper.writeValueAsString(moldeParaJson)));
        assertEquals(1, loteCreado.getDonaciones().size());
        entityManager().clear();
        assertEquals(1, donacionesRepository.cantidadInstancias());
        assertEquals(donacionesRepository.getInstancias().get(0).getIdDominio(),loteCreado.getDonaciones().get(0).getId());
      });
    }

  }

  private String pathBase() {
    return "/lotes";
  }

  private LoteDto leerIndividual(Response respuesta) throws IOException {
    return leerComo(respuesta, LoteDto.class);
  }

  private List<LoteDto> leerRespuestasMultiples(Response respuesta) throws IOException {
    return leerListaComo(respuesta, LoteDto.class);
  }

  private void chequearStatusCodeDeRequest(int statusCode, Map<String, Object> moldeParaJson) {
    JavalinTest.test(app, (server, client) -> {
      String json =  mapper.writeValueAsString(moldeParaJson);
      Response r = client.post(pathBase(), json);
      assertEquals(statusCode, r.code());
    });
  }

  private void registrarSubCategoria(SubCategoria subCategoria) {
    CategoriasRepository.getInstance().registrar(subCategoria.getCategoria());
    SubCategoriasRepository.getInstance().registrar(subCategoria);
  }

  private ItemDtoParaCreacionDeLote getItemSinFotosDeRemera(
    EstadoDeUso estadoDeUso,
    int cantidadDeUnidades
  ){
    return getItemSinFotos(
        BigDecimal.valueOf(1),
        estadoDeUso,
        cantidadDeUnidades,
        TipoBien.NOPERECEDERO,
        null,
        remera.getNombre()
    );
  }

  private ItemDtoParaCreacionDeLote getItemSinFotosDeRemeraNueva(int cantidadDeUnidades) {
    return getItemSinFotosDeRemera(EstadoDeUso.NUEVO, cantidadDeUnidades);
  }

  private ItemDtoParaCreacionDeLote getItemSinFotosDeRemeraUsada(int cantidadDeUnidades) {
    return getItemSinFotosDeRemera(EstadoDeUso.USADO, cantidadDeUnidades);
  }

  private ItemDtoParaCreacionDeLote getItemSinFotosDePaqueteDeArrozDeNGramaje(
      BigDecimal contenido,
      int cantidadDeUnidades,
      LocalDate fechaDeVencimiento
  ) {
    return getItemSinFotos(
        contenido,
        null,
        cantidadDeUnidades,
        TipoBien.PERECEDERO,
        fechaDeVencimiento,
        arroz.getNombre()
    );
  }

  private ItemDtoParaCreacionDeLote getItemSinFotoDePaqueteDeArrozDeNGramajeVenceEnUnMes(
      BigDecimal contenido,
      int cantidadDeUnidades
  ) {
    return getItemSinFotosDePaqueteDeArrozDeNGramaje(contenido, cantidadDeUnidades, FechasUtils.enUnMes());
  }

  private ItemDtoParaCreacionDeLote getItemSinFotoDePaqueteDeArrozDe500GramosVenceEnUnMes(int cantidadDeUnidades) {
    return  getItemSinFotoDePaqueteDeArrozDeNGramajeVenceEnUnMes(BigDecimal.valueOf(0.5), cantidadDeUnidades);
  }

  private ItemDtoParaCreacionDeLote getItemSinFotoDePaqueteDeArrozDe250GramosVenceEnUnMes(int cantidadDeUnidades) {
    return  getItemSinFotoDePaqueteDeArrozDeNGramajeVenceEnUnMes(BigDecimal.valueOf(0.25), cantidadDeUnidades);
  }

  private ItemDtoParaCreacionDeLote getItemSinFotos(
      BigDecimal contenido,
      EstadoDeUso estadoDeUso,
      int cantidadDeUnidades,
      TipoBien tipoBien,
      LocalDate fechaDeVencimiento,
      String nombreSubCategoria
  ) {
    return getItemDeCreacion(
        List.of(),
        contenido,
        estadoDeUso,
        cantidadDeUnidades,
        tipoBien,
        fechaDeVencimiento,
        nombreSubCategoria
    );
  }

  private ItemDtoParaCreacionDeLote getItemDeCreacion(
      List<String> fotos,
      BigDecimal contenido,
      EstadoDeUso estadoDeUso,
      int cantidadDeUnidades,
      TipoBien tipoBien,
      LocalDate fechaDeVencimiento,
      String nombreSubCategoria

  ) {
    return new ItemDtoParaCreacionDeLote()
        .setFotos(fotos)
        .setContenido(contenido)
        .setEstadoDeUso(estadoDeUso)
        .setCantidadDeUnidades(cantidadDeUnidades)
        .setTipoDeBien(tipoBien)
        .setFechaDeVencimiento(fechaDeVencimiento)
        .setNombreSubCategoria(nombreSubCategoria);
  }

}
