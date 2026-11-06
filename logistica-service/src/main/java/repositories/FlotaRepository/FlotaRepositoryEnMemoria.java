package repositories.FlotaRepository;

import domain.Camion;
import domain.Ruta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FlotaRepositoryEnMemoria implements FlotaRepository {
  // Usamos un Map para búsqueda rápida por Patente (ID natural del Camion)
  private final Map<String, Camion> camiones = new HashMap<>();
  public void registrar(Camion camion) {
    camiones.put(camion.getPatente(), camion);
  }

  public Camion buscarPorPatente(String patente) {
    return camiones.get(patente);
  }

  public List<Camion> obtenerTodos() {
    return new ArrayList<>(camiones.values());
  }

  public List<Camion> obtenerDisponibles() {
    return obtenerTodosSegun(Camion::estaDisponible);
  }

  public boolean existeCamion(String patente) {
    return camiones.containsKey(patente);
  }

  public List<Camion> obtenerEnCurso() {
    return obtenerTodosSegun(Camion::estaEnViaje);
  }

  public List<Camion> obtenerTodosSegun(Predicate<Camion> condicion) {
    return camiones.values().stream()
        .filter(condicion)
        .collect(Collectors.toList());
  }

  public Camion buscarPorRuta(Ruta unaRuta) {
    return obtenerTodosSegun((unCamion) -> unCamion.tieneAsignadaALaRuta(unaRuta)).stream().findAny().orElse(null);
  }

  public Camion obtenerPorId(Long id) {
    return obtenerTodosSegun((unCamion) -> unCamion.getId().equals(id)).stream().findFirst().orElse(null);
  }
}