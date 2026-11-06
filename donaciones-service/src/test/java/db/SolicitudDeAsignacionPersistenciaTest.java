package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Bienes.Bien;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;

public class SolicitudDeAsignacionPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private SolicitudDeAsignacion solicitudAPersistir;
  private DonacionIndependiente donacionIndependiente;
  private Bien remeraNueva;
  private EntidadBeneficiaria entidadBeneficiaria;

  @BeforeEach
  void setUp() {
    remeraNueva = FabricaDeBienes.remeraNueva();
    entidadBeneficiaria =  new EntidadBeneficiariaBuilder()
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1122334455")
            .conMail("dante@sendgrid.com")
            .build()
        )
        .conRazonSocial("salvar perros")
        .conNecesidades(List.of())
        .build();
    donacionIndependiente = FabricaDeDonacionesIndependientes.donacionConBienUnitario(remeraNueva);
    solicitudAPersistir = new SolicitudDeAsignacion(
        donacionIndependiente,
        List.of(entidadBeneficiaria)
    );
    withTransaction(() -> {
      entityManager().persist(donacionIndependiente.getDonante());
      entityManager().persist(remeraNueva.getSubCategoria().getCategoria());
      entityManager().persist(remeraNueva.getSubCategoria());
      entityManager().persist(entidadBeneficiaria);
      entityManager().persist(donacionIndependiente);
      entityManager().persist(solicitudAPersistir);
    });
  }

  @Test
  void debeTenerUnIdNoNulo() {
    assertNotNull(solicitudAPersistir.getId());
  }
}
