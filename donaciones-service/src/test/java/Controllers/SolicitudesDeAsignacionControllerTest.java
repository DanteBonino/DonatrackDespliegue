package Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoMixtoPorInterseccion;
import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Contacto.DatosDeContactoBuilder;
import Controllers.SolicitudesDeAsignacion.EjecutarAlgoritmo.SolicitudDeAsignacionResponseDto;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import Necesidades.Necesidad;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Injector.Injector;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SolicitudesDeAsignacion.SolicitudesDeAsignacionRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import ServiceLocator.ServiceLocator;
import Servidor.ServidorDonaciones;
import TareasProgramadas.CrearSolicitudesDeAsignacion.CrearSolicitudesDeAsignacion;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import utils.FabricaDeEntidadesBeneficiarias;

@ExtendWith(MockitoExtension.class)
class SolicitudesDeAsignacionControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {
  private CategoriasRepository categoriasRepository;
  private SubCategoriasRepository subCategoriasRepository;
  private DonantesRepository donantesRepository;
  private DonacionesRepository donacionesRepository;
  private BeneficiariosRepository beneficiariosRepository;
  private SolicitudesDeAsignacionRepository solicitudesDeAsignacionRepository;
  private CrearSolicitudesDeAsignacion crearSolicitudesDeAsignacion;

  @Mock
  private Injector inyectorDeDependencias;

  private Javalin app;

  @BeforeEach
  void setUp() {
    categoriasRepository = CategoriasRepository.getInstance();
    subCategoriasRepository = SubCategoriasRepository.getInstance();
    donantesRepository = DonantesRepository.getInstance();
    donacionesRepository = DonacionesRepository.getInstance();
    beneficiariosRepository = BeneficiariosRepository.getInstance();
    solicitudesDeAsignacionRepository = SolicitudesDeAsignacionRepository.getInstance();
    crearSolicitudesDeAsignacion = new CrearSolicitudesDeAsignacion(
        AlgoritmoMixtoPorInterseccion.algoritmoEstandar(),
        donacionesRepository
    );

    when(inyectorDeDependencias.getCrearSolicitudesDeAsignacion()).thenReturn(crearSolicitudesDeAsignacion);
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }

  @Nested
  class SinEntidadesPersistidas {
    // GET /solicitudes-de-asignacion
    @Test
    void obtenerTodasLasSolicitudesDeberiaDevolver200ComoStatusYUnaListaVacia() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get(pathBase());
        assertEquals(200, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas).isEmpty();
      });
    }

    // Post /solicitudes-de-asignacion/:id/aceptar
    @Test
    void aceptarUnaSolicitudEnviandoUnaEntidadBeneficiariaInexistenteDevuelve404ComoStatus() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathBase() + "/1/aceptar", jsonEntidadBenficiariaInexistente());
        assertEquals(404, respuesta.code());
      });
    }

    // POST /solicitudes-de-asignacion/ejecutar-algoritmo
    @Test
    void ejectuarAlgoritmoDevuelveLaListaVaciaSiNoHayEntidadesPersistidas() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas).isEmpty();
      });
    }

  }

  @Nested
  class ConSolicitudesPersistidas {
    private SolicitudDeAsignacion solicitudPersistida;
    private DonacionIndependiente cincuentaRemerasNuevas;
    private List<EntidadBeneficiaria> candidatas;
    private EntidadBeneficiaria entidadNoCandidata;

    @BeforeEach
    void setUp() {
      ServiceLocator.configurar(new PublicadorDeCambiosEstados());
      cincuentaRemerasNuevas = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.remeraNueva(), 50);
      candidatas = FabricaDeEntidadesBeneficiarias.crearCandidatasPara(10, cincuentaRemerasNuevas);
      solicitudPersistida = new SolicitudDeAsignacion(cincuentaRemerasNuevas, candidatas);
      entidadNoCandidata = FabricaDeEntidadesBeneficiarias.entidadSinNecesidadesEmpezandoEn(candidatas.size() + 1);
      withTransaction(() -> {
        categoriasRepository.registrar(cincuentaRemerasNuevas.getSubCategoria().getCategoria());
        subCategoriasRepository.registrar(cincuentaRemerasNuevas.getSubCategoria());
        candidatas.forEach(entidad -> {
          beneficiariosRepository.registrar(entidad);
        });
        donantesRepository.registrar(cincuentaRemerasNuevas.getDonante());
        donacionesRepository.registrar(cincuentaRemerasNuevas);
        solicitudesDeAsignacionRepository.registrar(solicitudPersistida);
        beneficiariosRepository.registrar(entidadNoCandidata);
      });
    }
    // GET /solicitudes-de-asignacion
    @Test
    void obtenerTodasLasSolicitudesDeberiaDevolver200ComoStatusYUnaListaConUnaSolicitud() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.get(pathBase());
        assertEquals(200, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas.size()).isEqualTo(1);
        SolicitudDeAsignacionResponseDto unicaSolicitud = respuestas.get(0);
        assertEquals(solicitudPersistida.getIdDominio(), unicaSolicitud.getId());
      });
    }
    // POST /solicitudes-de-asignacion/:id/aceptar
    @Test
    void aceptarUnaSolicitudInexistenteDevuelve404ComoStatus() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathBase() + "/1/aceptar", jsonConIdDeCandidata(algunaCandidata(candidatas).getIdDominio()));
        assertEquals(404, respuesta.code());
      });
    }

    @Test
    void aceptarUnaSolicitudDevuelve204() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathBase() +"/" + solicitudPersistida.getIdDominio() + "/aceptar", jsonConIdDeCandidata(algunaCandidata(candidatas).getIdDominio()));
        assertEquals(204, respuesta.code());
        SolicitudDeAsignacion solicitudActualizada = solicitudActualizada(solicitudPersistida.getIdDominio());
        assertTrue(solicitudActualizada.estaAceptada());
        donacionActualizadaEstaEn(cincuentaRemerasNuevas.getIdDominio(), EstadoDonacion.ASIGNACION_REALIZADA);
      });
    }

    @Test
    void noSePuedeAceptarUnaSolicitudQueYaFueAceptada() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathBase() +"/" + solicitudPersistida.getIdDominio() + "/aceptar", jsonConIdDeCandidata(algunaCandidata(candidatas).getIdDominio()));
        assertEquals(204, respuesta.code());
        SolicitudDeAsignacion solicitudActualizada = solicitudActualizada(solicitudPersistida.getIdDominio());
        assertTrue(solicitudActualizada.estaAceptada());
        donacionActualizadaEstaEn(cincuentaRemerasNuevas.getIdDominio(), EstadoDonacion.ASIGNACION_REALIZADA);
        Response nuevaRespuesta = cliente.post(pathBase() +"/" + solicitudPersistida.getIdDominio() + "/aceptar", jsonConIdDeCandidata(algunaCandidata(candidatas).getIdDominio()));
        assertEquals(400, nuevaRespuesta.code());
      });
    }

    @Test
    void noSePuedeAceptarUnaSolicitudSiSeEnviaElIdDeUnaEntidadQUeNoPerteneceALasCandidatas() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathBase() +"/" + solicitudPersistida.getIdDominio() + "/aceptar", jsonConIdDeCandidata(entidadNoCandidata.getIdDominio()));
        assertEquals(400, respuesta.code());
      });
    }

    // POST /solicitudes-de-asignacion/ejecutar-algoritmo
    @Test
    void ejectuarAlgoritmoDevuelveLaListaVaciaSiNoHayDonacionesEnDeposito() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas).isEmpty();
      });
    }
  }

  @Nested
  class SinEntidadesBeneficiariasQueMatcheenConLaDonacion {
    private DonacionIndependiente donacion;
    private List<EntidadBeneficiaria> noCandidatas;


    @BeforeEach
    void setUp() {
      donacion = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.remeraNueva(), 50);;
      noCandidatas = FabricaDeEntidadesBeneficiarias.crearEntidadesSinNecesidades(49);
      withTransaction(() -> {
        categoriasRepository.registrar(donacion.getSubCategoria().getCategoria());
        subCategoriasRepository.registrar(donacion.getSubCategoria());
        noCandidatas.forEach(entidad -> {
          beneficiariosRepository.registrar(entidad);
        });
        donantesRepository.registrar(donacion.getDonante());
        donacionesRepository.registrar(donacion);
      });
    }

    // POST /solicitudes-de-asignacion/ejecutar-algoritmo
    @Test
    void ejectuarAlgoritmoDevuelveLaListaVaciaSiNoHayEntidadesBeneficiariasQueMatcheen() {
      assertFalse(noCandidatas.stream().anyMatch(noCandidata -> noCandidata.tieneNecesidadPara(donacion)));
      assertEquals(49, beneficiariosRepository.getInstancias().size());
      assertEquals(1, donacionesRepository.getDonacionesEnDeposito().size());
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas).isEmpty();
      });
    }

  }

  @Nested
  class ConEntidadesBeneficiariasQueMatcheenConLaDonacion {
    private DonacionIndependiente donacion;
    private List<EntidadBeneficiaria> candidatas;


    @BeforeEach
    void setUp() {
      donacion = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.remeraNueva(), 50);
      candidatas = FabricaDeEntidadesBeneficiarias.crearNCandidatasPara(donacion, 9);
      withTransaction(() -> {
        categoriasRepository.registrar(donacion.getSubCategoria().getCategoria());
        subCategoriasRepository.registrar(donacion.getSubCategoria());
        candidatas.forEach(entidad -> {
          beneficiariosRepository.registrar(entidad);
        });
        donantesRepository.registrar(donacion.getDonante());
        donacionesRepository.registrar(donacion);
      });
    }

    // POST /solicitudes-de-asignacion/ejecutar-algoritmo
    @Test
    void siHay9EntidadesBeneficiariasQueSatisfacenUnaNecesidadConLaDonacionEntoncesSeCreaUnaSolicitudDondeTodasSonCandidatas() {
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas.size()).isEqualTo(1);
        SolicitudDeAsignacionResponseDto unicaSolicitud = respuestas.get(0);
        assertEquals(donacion.getIdDominio(), unicaSolicitud.getDonacion().getId());
        assertEquals(9, unicaSolicitud.getCandidatas().size());
        assertTrue(candidatas.stream().allMatch(candidata ->
            unicaSolicitud.getCandidatas().stream().anyMatch(respuestRecibida -> respuestRecibida.getId().equals(candidata.getIdDominio()))
        ));
      });
    }

    @Test
    void siHay11EntidadesBeneficiariasQueSatisfacenUnaNecesidadConLaDonacionEntoncesSeCreaUnaSolicitudDondeSolo10SonCandidatas() {
      List<EntidadBeneficiaria> nuevasEntidadesCandidatas = FabricaDeEntidadesBeneficiarias.crearCandidatasPara(11, donacion);
      withTransaction(() -> {
        candidatas.forEach(exCandidata -> {
          beneficiariosRepository.eliminarInstancia(exCandidata.getIdDominio());
        });
        nuevasEntidadesCandidatas.forEach(nuevaCandidata -> {
          beneficiariosRepository.registrar(nuevaCandidata);
        });
      });
      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas.size()).isEqualTo(1);
        SolicitudDeAsignacionResponseDto unicaSolicitud = respuestas.get(0);
        assertEquals(10, unicaSolicitud.getCandidatas().size());
        assertFalse(candidatas.stream().allMatch(candidata ->
            unicaSolicitud.getCandidatas().stream().anyMatch(respuestRecibida -> respuestRecibida.getId().equals(candidata.getIdDominio()))
        ));
      });
    }

    @Test
    void siHay9EntidadesBeneficiariasYDosDonacionesYTodasMatcheanConTodasSeCreanDosSolicitudesDeAsignacion() {
      DonacionIndependiente nuevaDonacion = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(donacion.getSubCategoria()));
      withTransaction(() -> {
        DonantesRepository.getInstance().registrar(nuevaDonacion.getDonante());
        donacionesRepository.registrar(nuevaDonacion);
      });

      JavalinTest.test(app, (server, cliente) -> {
        Response respuesta = cliente.post(pathEjecutarAlgoritmo());
        assertEquals(201, respuesta.code());
        List<SolicitudDeAsignacionResponseDto> respuestas = leerListaComo(respuesta, SolicitudDeAsignacionResponseDto.class);
        assertThat(respuestas.size()).isEqualTo(2);
      });
    }
  }

  // ---------- Helpers de dominio ----------
  private EntidadBeneficiaria unaEntidadDePrueba(String razonSocial, Necesidad unaNecesidad) {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial(razonSocial)
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of(unaNecesidad))
        .build();
  }

  private String pathBase() {
    return "/solicitudes-de-asignacion";
  }

  private String jsonVacio() {
    return """
        {}
        """;
  }

  private String jsonEntidadBenficiariaInexistente() {
    return jsonConIdDeCandidata("1122334455");
  }

  private String jsonConIdDeCandidata(String id) {
    return """
        { "idBeneficiario": "%s" }
        """.formatted(id);
  }

  private void donacionActualizadaEstaEn(String id, EstadoDonacion estadoEsperado) {
    assertTrue(donacionActualizada(id).estaEn(estadoEsperado));
  }

  private DonacionIndependiente donacionActualizada(String id) {
    entityManager().clear();
    return donacionesRepository.findById(id);
  }

  private SolicitudDeAsignacion solicitudActualizada(String id) {
    entityManager().clear();
    return solicitudesDeAsignacionRepository.findById(id);
  }

  private String pathEjecutarAlgoritmo() {
    return pathBase() + "/ejecutar-algoritmo";
  }

  private EntidadBeneficiaria algunaCandidata(List<EntidadBeneficiaria> entidadBeneficiarias) {
    return entidadBeneficiarias.stream().findAny().get();
  }
}
