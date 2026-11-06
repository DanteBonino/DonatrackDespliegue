package utils;

import Bienes.Bien;
import Donaciones.DonacionIndependiente;
import Donaciones.Item;
import Donantes.Donante;
import EstadoDonacion.EstadoDonacion;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import java.time.LocalDate;

public class FabricaDeDonacionesIndependientes {
  public static DonacionIndependiente nuevaDonacion(Item item, EstadoDonacion estado, Donante donante, PublicadorDeCambiosEstados publicador) {
    return new DonacionIndependiente(item, estado, donante, publicador);
  }

  public static DonacionIndependiente nuevaDonacionConBienConCiertaCantidadDeUnidadesDeUnDonante(Bien bien, int cantidadDeUnidades, Donante donante, EstadoDonacion estado, PublicadorDeCambiosEstados publicadorDeCambiosEstados) {
    return nuevaDonacion(FabricaDeItems.nuevoItemSinFotos(bien, cantidadDeUnidades), estado, donante, publicadorDeCambiosEstados);
  }

  public static DonacionIndependiente unaDonacionEnDeposito(Bien bien, int cantidad, Donante donante, PublicadorDeCambiosEstados publicador) {
    return nuevaDonacionConBienConCiertaCantidadDeUnidadesDeUnDonante(bien, cantidad, donante, EstadoDonacion.EN_DEPOSITO, publicador);
  }

  public static DonacionIndependiente unaDonacionSinObservers(Bien bien, int cantidad, Donante donante, EstadoDonacion estado) {
    return nuevaDonacionConBienConCiertaCantidadDeUnidadesDeUnDonante(bien, cantidad, donante, estado, new PublicadorDeCambiosEstados());
  }

  public static DonacionIndependiente donacionSinObserversYEnDeposito(Bien bien, int cantidad, Donante donante) {
    return unaDonacionSinObservers(bien, cantidad, donante, EstadoDonacion.EN_DEPOSITO);
  }

  public static DonacionIndependiente donacionConBienYCiertaCantidadDeUnidades(Bien bien, int cantidad) {
    return donacionSinObserversYEnDeposito(bien, cantidad, FabricaDeDatos.unDonanteDePrueba());
  }

  public static DonacionIndependiente donacionConBienUnitario(Bien bien) {
    return donacionConBienYCiertaCantidadDeUnidades(bien, 1);
  }

  public static DonacionIndependiente diezPaquetesDeArrozDe500GramosConVencimientoEn(LocalDate vencimiento) {
    return FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(vencimiento), 10);
  }

  public static DonacionIndependiente donacionConElMismoBienQue(DonacionIndependiente donacionIndependiente) {
    return donacionConBienUnitario(FabricaDeBienes.unBienComo(donacionIndependiente.getBienDonado()));
  }
}
