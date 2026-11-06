package TareasProgramadas.NotificarDonantesInactivos;

import InactividadDeDonante.PublicadorDeInactividad;
import Repositories.Donantes.DonantesRepository;
import TareasProgramadas.TareaProgramada;

public class NotificarDonantesInactivos extends TareaProgramada {
  private PublicadorDeInactividad publicadorDeInactividad;

  public NotificarDonantesInactivos(PublicadorDeInactividad publicadorDeInactividad) {
    this.publicadorDeInactividad = publicadorDeInactividad;
  }

  @Override
  protected void ejecutarTarea() {
    DonantesRepository.getInstance()
        .getInactivos()
        .forEach(this.publicadorDeInactividad::publicarInactividad);
  }

}
