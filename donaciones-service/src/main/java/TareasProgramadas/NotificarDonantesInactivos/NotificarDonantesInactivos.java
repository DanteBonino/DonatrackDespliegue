package TareasProgramadas.NotificarDonantesInactivos;

import InactividadDeDonante.PublicadorDeInactividad;
import Repositories.Donantes.DonantesRepository;
import Repositories.UsuariosRepository;
import TareasProgramadas.TareaProgramada;
import UsuarioFinal.UsuarioDonante;

public class NotificarDonantesInactivos extends TareaProgramada {
  private PublicadorDeInactividad publicadorDeInactividad;

  public NotificarDonantesInactivos(PublicadorDeInactividad publicadorDeInactividad) {
    this.publicadorDeInactividad = publicadorDeInactividad;
  }

  @Override
  protected void ejecutarTarea() {
    UsuariosRepository.getInstance()
        .getUsuariosDonantesInactivos()
        .stream().map(UsuarioDonante::getDonante)
        .forEach(this.publicadorDeInactividad::publicarInactividad);
  }
}