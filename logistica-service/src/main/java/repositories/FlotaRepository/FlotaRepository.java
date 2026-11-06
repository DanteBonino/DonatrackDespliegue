package repositories.FlotaRepository;

import domain.Camion;
import domain.Ruta;
import java.util.List;

public interface FlotaRepository {
  void registrar(Camion camion);
  Camion buscarPorPatente(String patente);
  List<Camion> obtenerTodos();
  List<Camion> obtenerDisponibles();
  List<Camion> obtenerEnCurso();
  Camion buscarPorRuta(Ruta unaRuta);
  Camion obtenerPorId(Long id);
}
