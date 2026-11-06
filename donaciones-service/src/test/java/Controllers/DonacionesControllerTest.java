package Controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static utils.FabricaDeDirecciones.calleCualquiera;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import Bienes.Bien;
import Contacto.DatosDeContactoBuilder;
import Controllers.Donanciones.Estados.AsignacionRealizada.DonacionAsignacionRealizadaResponseDto;
import Controllers.Donanciones.Estados.ListaParaEntregar.DonacionListaParaEntregarResponseDto;
import Controllers.Donanciones.Get.DonacionResponseDto;
import Controllers.Donanciones.HistorialDeCambiosDeEstado.CambioDeEstadoResponseDto;
import Donaciones.DonacionIndependiente;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Injector.Injector;
import Necesidades.Necesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import ServiceLocator.ServiceLocator;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDatos;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;
import utils.FechasUtils;

@ExtendWith(MockitoExtension.class)
class DonacionesControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  private CategoriasRepository categoriasRepository;
  private SubCategoriasRepository subCategoriasRepository;
  private DonacionesRepository donacionesRepositoryReal;
  private DonantesRepository donantesRepositoryReal;
  private BeneficiariosRepository beneficiariosRepositoryReal;

  @Mock
  private Injector inyectorDeDependencias;

  private Javalin app;

  @BeforeEach
  void setUp() {
    categoriasRepository = CategoriasRepository.getInstance();
    subCategoriasRepository = SubCategoriasRepository.getInstance();
    donacionesRepositoryReal = DonacionesRepository.getInstance();
    donantesRepositoryReal = DonantesRepository.getInstance();
    beneficiariosRepositoryReal = BeneficiariosRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }

  @Nested
  class SinDonacionesPersistidas {
    // ----- Get /donaciones
    @Test
    void siNoHayDonacionesGetAllDevuelveListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones");
        assertThat(respuestaHttp.code()).isEqualTo(200);
        List<DonacionResponseDto> respuestas = leerLista(respuestaHttp);
        assertThat(respuestas).isEmpty();
      });
    }

    // ----- Get /donaciones/:id
    @Test
    void siSeBuscaUnaDonacionInexistenteDevuelve404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones/id-inexistente");
        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }



  @Nested
  class ConDonacionPersistida {
    private DonacionIndependiente donacionPersistida;

    @BeforeEach
    void setUp() {
      donacionPersistida = unaDonacionDe50UnidadesDe(remeraNueva());
      registrarDonacion(donacionPersistida);
    }
    // ----- Get /donaciones
    @Test
    void siHayUnaDonacionGetAllDevuelveListaConEsaDonacion() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones");
        assertThat(respuestaHttp.code()).isEqualTo(200);
        List<DonacionResponseDto> donaciones = leerLista(respuestaHttp);
        assertEquals(1, donaciones.size());
        DonacionResponseDto primeraDonacion = donaciones.get(0);
        assertEquals(primeraDonacion.getId(), donacionPersistida.getIdDominio());
      });
    }

    // ----- Get /donaciones/:id
    @Test
    void siSeBuscaUnaDonacionExistenteDevuelve200() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones/" + donacionPersistida.getIdDominio());
        assertThat(respuestaHttp.code()).isEqualTo(200);
        DonacionResponseDto donacionComoRespuesta = leer(respuestaHttp);
        assertEquals(donacionPersistida.getIdDominio(), donacionComoRespuesta.getId());
      });

    }

    //----- DELETE /donaciones/:id
    @Test
    void siSeEliminaUnaDonacionExistenteDeberiaDevolver204YQuitarlaDelRepositorio() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donaciones/" + donacionPersistida.getIdDominio());
        assertThat(respuestaHttp.code()).isEqualTo(204);
        assertThat(donacionesRepositoryReal.getInstancias()).isEmpty();
      });
    }

    @Test
    void siSeIntentaEliminarUnaEntidadQueNoExisteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donaciones/CUALQUIER_COSA");
        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }


    // POST /donaciones/:id/vencer
    @Disabled
    @Test
    void vencerDevuelve204() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + donacionPersistida.getIdDominio() + "/vencida", "");
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(donacionPersistida.getIdDominio(), EstadoDonacion.VENCIDA);
      });
    }
  }

  @Nested
  class ConDonacionEliminada {
    private DonacionIndependiente donacionEliminada;

    @BeforeEach
    void setUp() {
      donacionEliminada = unaDonacionDe50UnidadesDe(remeraNueva());
      donacionEliminada.marcarComoEliminado();
      registrarDonacion(donacionEliminada);
    }

    // Get /donaciones/:id
    @Test
    void siSeBuscaUnaEntidadQueFueEliminadaDeberiaDevolver404() {

      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones/" + 12345312);

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });

    }

    @Test
    void noSePuedeEliminarUnaDonacionYaEliminada() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donaciones/" + donacionEliminada.getIdDominio());
        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    // =========================================================================
    // GET /donaciones
    // =========================================================================
    @Test
    void siLaUnicaDonacionPersistidaEstaEliminadaEntoncesDeberiaDevolverLaListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donaciones");
        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(leerLista(respuestaHttp)).isEmpty();
      });
    }

  }

  @Nested
  class ConDonacionPendienteDeSerAsignada {
    private DonacionIndependiente donacionPendienteDeSerAsignada;

    @BeforeEach
    void setUp() {
      donacionPendienteDeSerAsignada = unaDonacionDe50UnidadesDe(remeraNueva());
      donacionPendienteDeSerAsignada.enSolicitudDeAsignacion();
      registrarDonacion(donacionPendienteDeSerAsignada);
    }

    @Test
    void devuelve204SiSePasaAEnDeposito() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + donacionPendienteDeSerAsignada.getIdDominio() + "/en-deposito", "");
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(donacionPendienteDeSerAsignada.getIdDominio(), EstadoDonacion.EN_DEPOSITO);
      });
    }
  }

  @Nested
  class ConDonacionAsignada {
    private EntidadBeneficiaria asignado;
    private DonacionIndependiente donacionAsignada;

    @BeforeEach
    void setUp() {
      donacionAsignada = unaDonacionDe50UnidadesDe(remeraNueva());
      asignado = unaEntidadDePrueba(necesito100NuevosDeEstos(donacionAsignada.getBienDonado()));
      donacionAsignada.enSolicitudDeAsignacion();
      donacionAsignada.asignarA(asignado);
      registrarDonacion(donacionAsignada);
    }

    @Test
    void siSeAlistaParaEntregarUnaDonacionExistentePasaAListaParaEntregarYDevuelve204() {
      JavalinTest.test(app, (serve, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + donacionAsignada.getIdDominio() + "/lista-para-entregar", "{}");
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(donacionAsignada.getIdDominio(), EstadoDonacion.LISTA_PARA_ENTREGAR);
      });
    }

    @Test
    void siSeAlistaParaEntregarUnaDonacionInexistenteDevuelve404() {
      JavalinTest.test(app, (serve, client) -> {
        Response respuestaHttp = client.post("/donaciones/donacion-inexsistente/lista-para-entregar", "{}");
        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }
    @Disabled
    @Test
    void siHayUnaSolaDonacionAsignadaDonacionesAsignadasDevuelve200YTraeLaDonacion() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/asignadas");
        assertThat(respuesta.code()).isEqualTo(200);
        List<DonacionAsignacionRealizadaResponseDto> donacionesPendientes = leerListaComo(respuesta,DonacionAsignacionRealizadaResponseDto.class);
        assertEquals(1, donacionesPendientes.size());
        assertTrue(donacionesPendientes.stream().anyMatch(unaDonacion -> unaDonacion.getIdDonacion().equals(donacionAsignada.getIdDominio())));
      });
    }
  }

  @Nested
  class ConDonacionListaParaEntregar {
    private EntidadBeneficiaria asignado;
    private DonacionIndependiente listaParaEntregar;
    private String jsonValidoIniciarEntrega;
    private String jsonInvalido;

    @BeforeEach
    void setUp() {
      ServiceLocator.configurar(new PublicadorDeCambiosEstados());
      listaParaEntregar = unaDonacionDe50UnidadesDe(remeraNueva());
      asignado = unaEntidadDePrueba(necesito100NuevosDeEstos(listaParaEntregar.getBienDonado()));
      listaParaEntregar.enSolicitudDeAsignacion();
      listaParaEntregar.asignarA(asignado);
      listaParaEntregar.alistarParaEntregar();
      registrarDonacion(listaParaEntregar);

      jsonValidoIniciarEntrega = """
          { "idEntrega": "alguna-entrega-id" }
          """;
      jsonInvalido = "{}";
    }

    //Este test demuestra que el PostLoad o el constructor vacio funcionan
    @Test
    void iniciarElTrasladoDeUnaDonacionExistentePasaAEnTrasladoYDevuelve204() {
      JavalinTest.test(app, (serve, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + listaParaEntregar.getIdDominio() + "/en-traslado", jsonValidoIniciarEntrega);
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(listaParaEntregar.getIdDominio(), EstadoDonacion.EN_TRASLADO);
      });
    }

    @Test
    void iniciarElTrasladoDeUnaDonacionFallCon400SiNoSeEnviaElIdDeLaEntrega() {
      JavalinTest.test(app, (serve, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + listaParaEntregar.getIdDominio() + "/en-traslado", jsonInvalido);
        assertThat(respuestaHttp.code()).isEqualTo(400);
      });
    }

    @Test
    void iniciarElTrasladoDeUnaDonacionInexistenteDevuelve404() {
      JavalinTest.test(app, (serve, client) -> {
        Response respuestaHttp = client.post("/donaciones/donacion-inexsistente/en-traslado", jsonValidoIniciarEntrega);
        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    @Test
    void siHayUnaSolaDonacionListaParaEntregarListasParaEntregarDevuelve200YTraeLaDonacion() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/listas-para-entregar");
        assertThat(respuesta.code()).isEqualTo(200);
        List<DonacionListaParaEntregarResponseDto> donacionesPendientes = leerListaComo(respuesta,DonacionListaParaEntregarResponseDto.class);
        assertEquals(1, donacionesPendientes.size());
        assertTrue(donacionesPendientes.stream().anyMatch(unaDonacion -> unaDonacion.getIdDonacion().equals(listaParaEntregar.getIdDominio())));
      });
    }
  }

  @Nested
  class DonacionEnTraslado {
    private EntidadBeneficiaria asignado;
    private DonacionIndependiente enTraslado;
    private String jsonConComprobanteDeEntrega;
    private String jsonInvalido;
    private String jsonConJustificacion;

    @BeforeEach
    void setUp() {
      ServiceLocator.configurar(new PublicadorDeCambiosEstados());
      enTraslado = unaDonacionDe50UnidadesDe(remeraNueva());
      asignado = unaEntidadDePrueba(necesito100NuevosDeEstos(enTraslado.getBienDonado()));

      enTraslado.enSolicitudDeAsignacion();
      enTraslado.asignarA(asignado);
      enTraslado.alistarParaEntregar();
      enTraslado.iniciarTraslado("ENT-455");
      registrarDonacion(enTraslado);

      jsonConComprobanteDeEntrega = """
         {
            "comprobanteDeEntrega": {
              "timestamp": "2026-07-10T14:30:00",
              "patenteCamion": "AB123CD"
            }
          }
          """;
      jsonInvalido = "{}";
      jsonConJustificacion = """
          {
            "justificacion": "La entidad no pudo recibir la donación"
          }
          """;
    }
    //Entrgar con éxito
    @Test
    void marcarComoEntregadaSinComprobanteDevuelve400() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enTraslado.getIdDominio() + "/entregada", jsonInvalido);
        assertThat(respuestaHttp.code()).isEqualTo(400);
      });
    }
    @Test
    void marcarComoEntregadaConDatosValidosDevuelve204() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enTraslado.getIdDominio() + "/entregada", jsonConComprobanteDeEntrega);
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(enTraslado.getIdDominio(), EstadoDonacion.ENTREGADA);
      });
    }
    //Marcar como fallida la entrega
    @Test
    void marcarComoEntregaFallidaSinEnviarLaJustificacionDevuelve400() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enTraslado.getIdDominio() + "/entrega-fallida", jsonInvalido);
        assertThat(respuestaHttp.code()).isEqualTo(400);
      });
    }

    @Test
    void marcarComoEntregaFallidaEnviandoLaJustificacion204() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enTraslado.getIdDominio() + "/entrega-fallida", jsonConJustificacion);
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(enTraslado.getIdDominio(), EstadoDonacion.ENTREGA_FALLIDA);
      });
    }
    @Test
    void siNoHayNingunaDonacionEnEntregaFallidaPendientesDeRevisionDevuelve200YUnaListaVacia() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/pendientes-de-revision");
        assertThat(respuesta.code()).isEqualTo(200);
        List<DonacionResponseDto> donacionesPendientes = leerLista(respuesta);
        assertThat(donacionesPendientes).isEmpty();
      });
    }
    @Disabled
    @Test
    void siNoHayNingunaDonacionAsignadaAsignadasDevuelve200YUnaListaVacia() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/asignadas");
        assertThat(respuesta.code()).isEqualTo(200);
        List<DonacionResponseDto> donacionesPendientes = leerLista(respuesta);
        assertThat(donacionesPendientes).isEmpty();
      });
    }
  }

  @Nested
  class DonacionEnEntregaFallida {
    private EntidadBeneficiaria asignado;
    private DonacionIndependiente enEntregaFallida;

    @BeforeEach
    void setUp() {
      enEntregaFallida = unaDonacionDe50UnidadesDe(remeraNueva());
      asignado = unaEntidadDePrueba(necesito100NuevosDeEstos(enEntregaFallida.getBienDonado()));

      enEntregaFallida.enSolicitudDeAsignacion();
      enEntregaFallida.asignarA(asignado);
      enEntregaFallida.alistarParaEntregar();
      enEntregaFallida.iniciarTraslado("ENT-455");
      enEntregaFallida.marcarComoFallida("No se encontró la casa");
      registrarDonacion(enEntregaFallida);
    }

    @Test
    void devuelve204SiSePasaAEnDeposito() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enEntregaFallida.getIdDominio() + "/en-deposito", "");
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(enEntregaFallida.getIdDominio(), EstadoDonacion.EN_DEPOSITO);
      });
    }
    @Disabled
    @Test
    void vencerDevuelve204() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.post("/donaciones/" + enEntregaFallida.getIdDominio() + "/vencida", "");
        assertThat(respuestaHttp.code()).isEqualTo(204);
        elActualizadoEstaEn(enEntregaFallida.getIdDominio(), EstadoDonacion.VENCIDA);
      });
    }

    @Test
    void siHayUnaSolaDonacionEnEntregaFallidaPendientesDeRevisionDevuelve200YTraeLaDonacion() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/pendientes-de-revision");
        assertThat(respuesta.code()).isEqualTo(200);
        List<DonacionResponseDto> donacionesPendientes = leerLista(respuesta);
        assertEquals(1, donacionesPendientes.size());
        assertTrue(donacionesPendientes.stream().anyMatch(unaDonacion -> unaDonacion.getId().equals(enEntregaFallida.getIdDominio())));
      });
    }
  }

  @Nested
  class HistorialDeEstado {
    private DonacionIndependiente donacionQueNoTransicionaUnaVezInstancia;
    private DonacionIndependiente enEntregaFallida;
    private EntidadBeneficiaria asignado;

    @BeforeEach
    void setUp() {
      enEntregaFallida =  unaDonacionDe50UnidadesDe(remeraNueva());
      donacionQueNoTransicionaUnaVezInstancia = unaDonacionDe50UnidadesDe(FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(FechasUtils.enUnMes()));
      asignado = unaEntidadDePrueba(necesito100NuevosDeEstos(enEntregaFallida.getBienDonado()));


      enEntregaFallida.enSolicitudDeAsignacion();
      enEntregaFallida.asignarA(asignado);
      enEntregaFallida.alistarParaEntregar();
      enEntregaFallida.iniciarTraslado("ENT-455");
      enEntregaFallida.marcarComoFallida("No se encontró la casa");
      registrarDonacion(enEntregaFallida);
      registrarDonacion(donacionQueNoTransicionaUnaVezInstancia);
    }

    @Test
    void elHistorialDeCambiosDeEstadoDeLaDonacionQueNoTransicionoTieneUnSoloElemento() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/" + donacionQueNoTransicionaUnaVezInstancia.getIdDominio() + "/historial-cambios-de-estado");
        assertThat(respuesta.code()).isEqualTo(200);
        List<CambioDeEstadoResponseDto> cambiosDeEstadoRecibido = leerListaComo(respuesta, CambioDeEstadoResponseDto.class);
        assertEquals(1, cambiosDeEstadoRecibido.size());
        CambioDeEstadoResponseDto unicoCambio = cambiosDeEstadoRecibido.get(0);
        assertNull(unicoCambio.getIdEntidadAsignada());
        assertEquals(EstadoDonacion.EN_DEPOSITO, unicoCambio.getTipo());
      });
    }

    @Test
    void elHistorialDeCambiosDeEstadoDeLaDonacionEnEntregaFallidaTiene6Elementos() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get("/donaciones/" + enEntregaFallida.getIdDominio() + "/historial-cambios-de-estado");
        assertThat(respuesta.code()).isEqualTo(200);
        List<CambioDeEstadoResponseDto> cambiosDeEstadoRecibido = leerListaComo(respuesta, CambioDeEstadoResponseDto.class);
        assertEquals(6, cambiosDeEstadoRecibido.size());
        CambioDeEstadoResponseDto ultimoCambio = cambiosDeEstadoRecibido.get(cambiosDeEstadoRecibido.size()-1);
        assertEquals(asignado.getIdDominio(),ultimoCambio.getIdEntidadAsignada());
        assertEquals(EstadoDonacion.ENTREGA_FALLIDA, ultimoCambio.getTipo());
      });
    }


  }

  // ---------- Helpers de dominio ----------

  private PersonaHumana unDonanteDePrueba() {
    return new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("dante@example.com")
            .conTelefono("1120304050")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Dante", "Perez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(calleCualquiera())
        .build();
  }

  private Bien remeraNueva() {
    return FabricaDeBienes.remeraNueva();
  }

  private EntidadBeneficiaria unaEntidadDePrueba(Necesidad unaNecesidad) {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of(unaNecesidad))
        .build();
  }


  private DonacionIndependiente unaDonacionDe50UnidadesDe(Bien bien) {
    return FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(bien, 50);
  }

  private Necesidad necesito100NuevosDeEstos(Bien unBien) {
    return FabricaDeNecesidades.necesidadEnEstadoNuevo(unBien.getSubCategoria(), 100);
  }

  private DonacionResponseDto leer(Response unaRespuesta) throws IOException {
    return leerComo(unaRespuesta, DonacionResponseDto.class);
  }

  private List<DonacionResponseDto> leerLista(Response unaRespuesta) throws IOException {
    return leerListaComo(unaRespuesta, DonacionResponseDto.class);
  }

  private void elActualizadoEstaEn(String id, EstadoDonacion estadoEsperado) {
    assertTrue(actualizado(id).estaEn(estadoEsperado));
  }

  private DonacionIndependiente actualizado(String id) {
    entityManager().clear();
    return donacionesRepositoryReal.findById(id);
  }

  private void registrarDonacionSinTransaccion(DonacionIndependiente donacionIndependiente) {
    donantesRepositoryReal.registrar(donacionIndependiente.getDonante());
    categoriasRepository.registrar(donacionIndependiente.getSubCategoria().getCategoria());
    subCategoriasRepository.registrar(donacionIndependiente.getSubCategoria());
    if(donacionIndependiente.getAsignado() != null) beneficiariosRepositoryReal.registrar(donacionIndependiente.getAsignado());
    donacionesRepositoryReal.registrar(donacionIndependiente);
  }

  private void registrarDonacion(DonacionIndependiente donacionIndependiente) {
    withTransaction(() -> {
      registrarDonacionSinTransaccion(donacionIndependiente);
    });
    }
  }
}