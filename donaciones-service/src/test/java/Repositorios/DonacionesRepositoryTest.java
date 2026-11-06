package Repositorios;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDatos;
import utils.FabricaDeDonacionesIndependientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static utils.FabricaDeDirecciones.calleCualquiera;
import static utils.FechasUtils.enNDias;

@ExtendWith(MockitoExtension.class)
public class DonacionesRepositoryTest implements ConLimpiezaDeBaseDeDatos {
  private DonacionesRepository donacionesRepository;
  private DonacionIndependiente diezPaqueteDeArrozDe500Gramos;
  private DonantesRepository donantesRepository;
  private CategoriasRepository categoriasRepository;
  private SubCategoriasRepository subCategoriasRepository;
  private Donante donante = new DonanteHumanoBuilder()
      .conContacto(new DatosDeContactoBuilder()
          .conTelefono("1120304050")
          .conMail("fabio@dot.com")
          .build()
      )
      .conDocumento("45459090").conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
      .conNombreCompleto(new NombreCompleto("Fabio", "Alvarez")).conGenero(Genero.MASCULINO)
      .conCalle(calleCualquiera())
      .build();
  @BeforeEach
  void setUp() {
    donacionesRepository = DonacionesRepository.getInstance();
    categoriasRepository = CategoriasRepository.getInstance();
    subCategoriasRepository = SubCategoriasRepository.getInstance();
  }
  @Nested
  class ConPublicadorNoVacio {
    private InteresadoEnCambiosDeEstado unInteresado;
    private PublicadorDeCambiosEstados publicadorNoVacio;

    @BeforeEach
    void setUp() {
      unInteresado = mock(InteresadoEnCambiosDeEstado.class);
      publicadorNoVacio = new PublicadorDeCambiosEstados();
      publicadorNoVacio.suscribir(unInteresado);
      diezPaqueteDeArrozDe500Gramos = FabricaDeDonacionesIndependientes.unaDonacionEnDeposito(FabricaDeBienes.remeraNueva(), 10, FabricaDeDatos.unDonanteDePrueba(), publicadorNoVacio);
      donacionesRepository = DonacionesRepository.getInstance();
      withTransaction(() -> {
        registrarDonacion(diezPaqueteDeArrozDe500Gramos);
      });
    }

    @Test
    void lasDonacionesCreadasPorElRepositorioNotificanAlInteresadoAlAsignarse(){
      DonacionIndependiente primeraDonacion = donacionesRepository.getInstancias().get(0);
      EntidadBeneficiaria unaEntidad = mock(EntidadBeneficiaria.class);

      primeraDonacion.enSolicitudDeAsignacion();
      primeraDonacion.asignarA(unaEntidad);

      verify(unInteresado).anteAsignacion(primeraDonacion, unaEntidad);
    }

    @Test
    void lasDonacionesCreadasPorElRepositoryNotificanAlInteresadoAlIniciarseLaRuta() {
      DonacionIndependiente primeraDonacion = donacionesRepository.getInstancias().get(0);
      EntidadBeneficiaria unaEntidad = mock(EntidadBeneficiaria.class);

      primeraDonacion.enSolicitudDeAsignacion();
      primeraDonacion.asignarA(unaEntidad);
      primeraDonacion.alistarParaEntregar();
      primeraDonacion.iniciarTraslado("ENT-404");
      verify(unInteresado).anteInicioDeRuta(primeraDonacion, unaEntidad, "ENT-404");
    }

    @Test
    void lasDonacionesCreadasPorElRepositoryNotificanAlInteresadoAlEntregarConExito() {
      DonacionIndependiente primeraDonacion = donacionesRepository.getInstancias().get(0);
      EntidadBeneficiaria unaEntidad = mock(EntidadBeneficiaria.class);

      primeraDonacion.enSolicitudDeAsignacion();
      primeraDonacion.asignarA(unaEntidad);
      primeraDonacion.alistarParaEntregar();
      primeraDonacion.iniciarTraslado("ENT-404");
      ComprobanteDeEntrega comprobanteDeEntrega = new ComprobanteDeEntrega(LocalDateTime.now(), "AAA-111");
      primeraDonacion.marcarComoEntregada(comprobanteDeEntrega);
      verify(unInteresado).anteEntregaConExito(primeraDonacion, unaEntidad, comprobanteDeEntrega);
    }

    @Test
    void lasDonacionesCreadasPorElRepositoryNotificanAlInteresadoAlFallarEnLaEntrega() {
      DonacionIndependiente primeraDonacion = donacionesRepository.getInstancias().get(0);
      EntidadBeneficiaria unaEntidad = mock(EntidadBeneficiaria.class);

      primeraDonacion.enSolicitudDeAsignacion();
      primeraDonacion.asignarA(unaEntidad);
      primeraDonacion.alistarParaEntregar();
      primeraDonacion.iniciarTraslado("ENT-404");
      primeraDonacion.marcarComoFallida("fallamos");
      verify(unInteresado).anteEntregaFallida("fallamos", unaEntidad, primeraDonacion);
    }
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
