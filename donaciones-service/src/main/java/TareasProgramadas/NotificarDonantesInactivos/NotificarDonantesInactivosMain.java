package TareasProgramadas.NotificarDonantesInactivos;

import InactividadDeDonante.NotificadorDeInactividad;
import InactividadDeDonante.PublicadorDeInactividad;
import Injector.Injector;
import utils.BaseDeDatos;

public class NotificarDonantesInactivosMain {
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    new NotificarDonantesInactivos(new Injector().getPublicadorDeInactividad()).ejecutar();
  }
}
