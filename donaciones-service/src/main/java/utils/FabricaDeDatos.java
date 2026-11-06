package utils;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import Necesidades.Necesidad;
import Necesidades.NecesidadExtraordinariaBuilder;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Donaciones.DonacionesRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class FabricaDeDatos {
  public static Donante unDonanteDePrueba() {
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
        .conCalle(FabricaDeDirecciones.calleCualquiera())
        .build();
  }

  public static EntidadBeneficiaria unaEntidadDePrueba(String razonSocial, Necesidad unaNecesidad) {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial(razonSocial)
        .conDireccion(FabricaDeDirecciones.direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of(unaNecesidad))
        .build();
  }

  public static Donante unDonanteDePruebaConDatosDeContactoTrackeables() {
    return new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("donatrackgrupo1@gmail.com")
            .conTelefono("1140811647")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Dante", "Perez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(FabricaDeDirecciones.calleCualquiera())
        .build();
  }

  //Cosas que Sí o Sí tienen que estar acá:
  public static <T> List<T> crearN(int n, IntFunction<T> creadora) {
    return IntStream.rangeClosed(1, n).mapToObj(creadora).toList();
  }

  private static final Random RANDOM = new Random(42);

  public static int entre(int minInclusive, int maxInclusive) {
    return RANDOM.nextInt(minInclusive, maxInclusive + 1);
  }
}
