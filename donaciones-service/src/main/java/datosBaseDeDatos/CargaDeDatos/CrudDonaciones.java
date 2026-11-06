package datosBaseDeDatos.CargaDeDatos;

import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Injector.Injector;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Usuario.Administrador;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import utils.FabricaDeBienes;
import utils.FabricaDeDatos;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;

public class CrudDonaciones extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CrudDonaciones().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    cargarEscenario();
  }

  public void cargarEscenario() {
    SubCategoriasRepository subCategoriasRepository = SubCategoriasRepository.getInstance();
    DonantesRepository donantesRepository = DonantesRepository.getInstance();
    BeneficiariosRepository beneficiariosRepository = BeneficiariosRepository.getInstance();
    AdministradoresRepository adminsRepository = AdministradoresRepository.getInstance();
    DonacionesRepository donacionesRepository = DonacionesRepository.getInstance();

    SubCategoria remera = subCategoriasRepository.findByNombre("remera");

    withTransaction(() -> {
      adminsRepository.registrar(new Administrador(new DatosDeContactoBuilder().conMail("dantebonino3@gmail.com").conTelefono("1122334455").build()));
    });


    DonacionIndependiente donacionEnDeposito = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    DonacionIndependiente donacionEnPendienteDeAsignacion = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionEnPendienteDeAsignacion.enSolicitudDeAsignacion();
    DonacionIndependiente donacionAsignada = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionAsignada.enSolicitudDeAsignacion();
    EntidadBeneficiaria asignado = FabricaDeEntidadesBeneficiarias.entidadSinNecesidadesEmpezandoEn(20);
    FabricaDeEntidadesBeneficiarias.agregarNNecesidadesCompatiblesConA(50, donacionAsignada, asignado);
    donacionAsignada.asignarA(asignado);
    DonacionIndependiente donacionListaParaEntrega = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionListaParaEntrega.enSolicitudDeAsignacion();
    donacionListaParaEntrega.asignarA(asignado);
    donacionListaParaEntrega.alistarParaEntregar();
    DonacionIndependiente donacionEnTraslado = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionEnTraslado.enSolicitudDeAsignacion();
    EntidadBeneficiaria asignadoUnicamenteParaEntraslado = FabricaDeEntidadesBeneficiarias.crearCandidataPara(donacionEnTraslado);
    donacionEnTraslado.asignarA(asignadoUnicamenteParaEntraslado);
    donacionEnTraslado.alistarParaEntregar();
    donacionEnTraslado.iniciarTraslado("ENT-404");
    DonacionIndependiente donacionEntregada = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionEntregada.enSolicitudDeAsignacion();
    donacionEntregada.asignarA(asignado);
    donacionEntregada.alistarParaEntregar();
    donacionEntregada.iniciarTraslado("ENT-200");
    donacionEntregada.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), "CCC-333"));
    DonacionIndependiente donacionFallida = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionFallida.enSolicitudDeAsignacion();
    donacionFallida.asignarA(asignado);
    donacionFallida.alistarParaEntregar();
    donacionFallida.iniciarTraslado("ENT-500");
    donacionFallida.marcarComoFallida("No se pudo");
    DonacionIndependiente donacionVencida = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(remera));
    donacionVencida.vencer();
    withTransaction(() -> {
      beneficiariosRepository.registrarInstancias(List.of(asignado, asignadoUnicamenteParaEntraslado));
      List.of(donacionEnDeposito, donacionEnPendienteDeAsignacion, donacionAsignada, donacionListaParaEntrega, donacionEnTraslado, donacionEntregada, donacionFallida, donacionVencida).forEach(
          this::registrarDonacion
      );
    });
  }

  private void registrarDonacion(DonacionIndependiente donacionIndependiente) {
    DonantesRepository.getInstance().registrar(donacionIndependiente.getDonante());
    DonacionesRepository.getInstance().registrar(donacionIndependiente);
  }
}
