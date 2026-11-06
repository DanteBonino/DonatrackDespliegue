package utils;
import static utils.FabricaDeDatos.crearN;
import Contacto.DatosDeContactoBuilder;
import Direccion.Posicion;
import Direccion.builders.CalleBuilder;
import Direccion.builders.DireccionBuilder;
import Direccion.builders.PosicionBuilder;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Necesidades.Necesidad;
import java.util.List;
import java.util.function.Supplier;

public class FabricaDeEntidadesBeneficiarias {
  public static EntidadBeneficiaria unaEntidad(int salt, List<Necesidad> necesidades) {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Entidad " + salt)
        .conDireccion(
            new DireccionBuilder()
                .conCalle(
                    new CalleBuilder()
                        .conNombre("Avenida Rivadavia")
                        .conAltura(1000 + salt)
                        .build()
                )
                .conPosicion(
                    new PosicionBuilder()
                        .conLongitud(-58.380474 + salt)
                        .conLatitud(-34.608398 + salt)
                        .build()
                )
                .build()
        )
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono(String.format("11%08d", salt))
            .conMail("entidad" + salt + "@test.org")
            .build())
        .conNecesidades(necesidades)
        .build();
  }


  public static List<EntidadBeneficiaria> crearNCandidatasPara(DonacionIndependiente donacion, int cantidad) {
    return crearNEntidadesBeneficiariasConNecesidades(
        cantidad,
        () -> List.of(FabricaDeNecesidades.necesidadVigenteDe(
            donacion.getSubCategoria(),
            FabricaDeDatos.entre(5, 15))),
        0
    );
  }

  public static List<EntidadBeneficiaria> crearEntidadesSinNecesidadesEmpezandoEn(int cantidadDeBeneficiarias, int saltInicial) {
    return crearNEntidadesBeneficiariasConNecesidades(cantidadDeBeneficiarias, List::of, saltInicial);
  }

  public static List<EntidadBeneficiaria> crearEntidadesSinNecesidades(int cantidadDeBeneficiarias) {
    return crearEntidadesSinNecesidadesEmpezandoEn(cantidadDeBeneficiarias, 0);
  }

  private static List<EntidadBeneficiaria> crearNEntidadesBeneficiariasConNecesidades(int cantidadDeBeneficiarias, Supplier<List<Necesidad>> conNecesidades, int saltInicial) {
    return crearN(cantidadDeBeneficiarias, i -> unaEntidad(i + saltInicial, conNecesidades.get()));
  }

  public static void agregarNNecesidadesCompatiblesConA(int cantidadDeNecesidades, DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria) {
    FabricaDeNecesidades.nNecesidadesVigentesDe(donacionIndependiente.getSubCategoria(), cantidadDeNecesidades)
        .forEach(entidadBeneficiaria::agregarNecesidad);
  }

  public static List<EntidadBeneficiaria> crearCandidatasPara(int cantidadDeCandidatas, DonacionIndependiente donacionIndependiente) {
    List<EntidadBeneficiaria> entidades = crearEntidadesSinNecesidades(cantidadDeCandidatas);
    entidades.forEach(entidad -> agregarNNecesidadesCompatiblesConA(1, donacionIndependiente, entidad));
    return entidades;
  }

  public static EntidadBeneficiaria crearCandidataPara(DonacionIndependiente donacion) {
    return crearCandidatasPara(1, donacion).get(0);
  }

  public static EntidadBeneficiaria entidadSinNecesidades() {
    return crearEntidadesSinNecesidades(1).get(0);
  }

  public static EntidadBeneficiaria entidadSinNecesidadesEmpezandoEn(int saltInicial) {
    return crearEntidadesSinNecesidadesEmpezandoEn(1, saltInicial).get(0);
  }

}
