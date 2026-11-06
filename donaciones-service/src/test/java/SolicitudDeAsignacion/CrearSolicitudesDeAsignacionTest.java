package SolicitudDeAsignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeEntidadesBeneficiarias.crearEntidadesSinNecesidades;
import static utils.FabricaDeEntidadesBeneficiarias.crearNCandidatasPara;
import static utils.FechasUtils.enNDias;

import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoMixtoPorInterseccion;
import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Bienes.Bien;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Beneficiarios.IBeneficiariosRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Donantes.IDonantesRepository;
import Repositories.SolicitudesDeAsignacion.ISolicitudesDeAsignacionRepository;
import Repositories.SolicitudesDeAsignacion.SolicitudesDeAsignacionRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import TareasProgramadas.CrearSolicitudesDeAsignacion.CrearSolicitudesDeAsignacion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDatos;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;

class CrearSolicitudesDeAsignacionTest implements ConLimpiezaDeBaseDeDatos {

  private DonacionesRepository donacionesRepository;
  private IBeneficiariosRepository beneficiariosRepository;
  private ISolicitudesDeAsignacionRepository solicitudesDeAsignacionRepository;

  private CrearSolicitudesDeAsignacion crearSolicitudes;

  @BeforeEach
  void setUp() {
    donacionesRepository = DonacionesRepository.getInstance();
    beneficiariosRepository = BeneficiariosRepository.getInstance();
    solicitudesDeAsignacionRepository = SolicitudesDeAsignacionRepository.getInstance();

    crearSolicitudes = new CrearSolicitudesDeAsignacion(
        AlgoritmoMixtoPorInterseccion.algoritmoEstandar(),
        donacionesRepository
    );
  }
  @Nested
  class SinEntidadesPersistidas {
    @Test
    void NoSeDebenCrearSolicitudesDeAsignacion() {
      assertEquals(0, crearSolicitudes.ejecutar().size());
    }
  }

  @Nested
  class ConEntidadesPersistidasYSinDonacion {
    List<EntidadBeneficiaria> entidades;
    @BeforeEach
    void setUp() {
      entidades = FabricaDeEntidadesBeneficiarias.crearEntidadesSinNecesidades(10);
      withTransaction(() -> {
        registrarEntidades(entidades);
      });
    }
    @Test
    void siNoHayDonacionesEnDepositoDevuelveLaListaVacia() {
      assertEquals(0, crearSolicitudes.ejecutar().size());
    }
  }

  @Nested
  class ConEntidadesPersistidasQueMatcheaYUnaDonacion {
    private DonacionIndependiente diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes;
    private List<EntidadBeneficiaria> candidatas;

    @BeforeEach
    void setUp() {
      diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(enNDias(31));
      withTransaction(() -> {
       registrarDonacion(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes);
      });
    }

    @Test
    void siHayUnaEntidadQueMatcheaEntoncesSeCreaUnaSolicitudDeAsignacion() {
      registrarEntidades(crearNCandidatasPara(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes, 1));
      assertEquals(1, crearSolicitudes.ejecutar().size());
    }

    @Test
    void siSeCreaLaSolicitudDeAsignacionEntoncesLaDonacionAsociadaPasaAEstarPendienteDeAsignacion() {
      registrarEntidades(crearNCandidatasPara(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes, 1));
      SolicitudDeAsignacion solicitudCreada = crearSolicitudes.ejecutar().get(0);
      assertEquals(EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION, solicitudCreada.getDonacion().getEstado());
    }

    @Test
    void siHay11EntidadQueMatcheanEntoncesSeCreaUnaSolicitudDeAsignacionYAlgunaNoVaASerCandidata() {
      candidatas = crearNCandidatasPara(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes, 11);
      registrarEntidades(candidatas);
      List<SolicitudDeAsignacion> solicitudesDeAsignacion = crearSolicitudes.ejecutar();
      assertEquals(1, solicitudesDeAsignacion.size());
      System.out.println("cantidad de candidatas " + solicitudesDeAsignacion.get(0).getCandidatas().size());
      assertFalse(candidatas.stream().allMatch(candidata ->
          solicitudesDeAsignacion.get(0).getCandidatas().stream().anyMatch(respuestRecibida -> respuestRecibida.getId().equals(candidata.getIdDominio()))
      ));
    }

    @Test
    void siNoHayCandidatasNoSeCreanSolicitudes() {
      candidatas = crearNoCandidatas();
      registrarEntidades(candidatas);
      assertEquals(0, crearSolicitudes.ejecutar().size());
    }
  }

  @Nested
  class ConMultiplesDonacionesPersistidas {
    private DonacionIndependiente unaDonacion;
    private DonacionIndependiente otraDonacion;
    private DonacionIndependiente donacionDeReferenciaParaLasNecesidades;
    private List<EntidadBeneficiaria> candidatas;
    @BeforeEach
    void setUp() {
      donacionDeReferenciaParaLasNecesidades = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(enNDias(31));
      candidatas = FabricaDeEntidadesBeneficiarias.crearCandidatasPara(10, donacionDeReferenciaParaLasNecesidades);
      withTransaction(() -> {
        registroSetUpDonacion(donacionDeReferenciaParaLasNecesidades);
        registrarEntidades(candidatas);
      });
    }

    @Nested
    class AmbasDonacionesNoMatchean {
      @BeforeEach
      void setUp() {
        unaDonacion = FabricaDeDonacionesIndependientes.donacionConBienUnitario(bienQueNoMatchea());
        otraDonacion = FabricaDeDonacionesIndependientes.donacionConElMismoBienQue(unaDonacion);
        withTransaction(() -> {
          registrarDonacion(unaDonacion);
          DonantesRepository.getInstance().registrar(otraDonacion.getDonante());
          donacionesRepository.registrar(otraDonacion);
        });
      }

      @Test
      void siLasDonacionesExistentesNoMatcheanConLasNecesidadesDeNingunaCandidataNoSeCreanSolicitudes() {
        assertEquals(0, crearSolicitudes.ejecutar().size());
      }
    }

    @Nested
    class UnaMatcheaLaOtraNo {
      @BeforeEach
      void setUp() {
        unaDonacion = FabricaDeDonacionesIndependientes.donacionConElMismoBienQue(donacionDeReferenciaParaLasNecesidades);
        otraDonacion = FabricaDeDonacionesIndependientes.donacionConBienUnitario(bienQueNoMatchea());
        withTransaction(() -> {
          registrarDonacion(unaDonacion);
          registrarDonacion(otraDonacion);
        });
      }
      @Test
      void seCreaUnaSolicitudSola() {
        assertEquals(1, crearSolicitudes.ejecutar().size());
      }
    }

    @Nested
    class AmbasMatcheanConTodasLasEntidades {
      @BeforeEach
      void setUp() {
        unaDonacion = FabricaDeDonacionesIndependientes.donacionConElMismoBienQue(donacionDeReferenciaParaLasNecesidades);
        otraDonacion = FabricaDeDonacionesIndependientes.donacionConElMismoBienQue(donacionDeReferenciaParaLasNecesidades);
        withTransaction(() -> {
          registrarDonacion(unaDonacion);
          registrarDonacion(otraDonacion);
        });
      }
      @Test
      void seCreanDosSolicitudes() {
        assertEquals(2, crearSolicitudes.ejecutar().size());
      }
    }
  }
  //Helpers
  private List<EntidadBeneficiaria> crearNoCandidatas() {
    return crearEntidadesSinNecesidades(50);
  }

  private void registrarEntidades (List<EntidadBeneficiaria> entidades) {
    withTransaction(() -> {
      entidades.forEach(unaEntidad -> {
        beneficiariosRepository.registrar(unaEntidad);
      });
    });
  }

  private EntidadBeneficiaria algunaCandidata(List<EntidadBeneficiaria> candidatas) {
    return candidatas.stream().findAny().get();
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

  private Bien bienQueNoMatchea() {
    return FabricaDeBienes.remeraNueva();
  }

  private void registrarDonacion(DonacionIndependiente donacion) {
    registroSetUpDonacion(donacion);
    DonacionesRepository.getInstance().registrar(donacion);
  }

  private void registroSetUpDonacion(DonacionIndependiente donacion) {
    DonantesRepository.getInstance().registrar(donacion.getDonante());
    CategoriasRepository.getInstance().registrar(donacion.getSubCategoria().getCategoria());
    SubCategoriasRepository.getInstance().registrar(donacion.getSubCategoria());
  }

}