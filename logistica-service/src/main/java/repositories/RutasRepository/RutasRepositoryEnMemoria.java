package repositories.RutasRepository;

import domain.Entrega;
import domain.EstadoRuta;
import domain.Ruta;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RutasRepositoryEnMemoria implements RutasRepository {
  private final Map<String, Ruta> rutas = new HashMap<>();

  public void registrar(Ruta ruta) {
    rutas.put(ruta.getNombre(), ruta);
  }

  public List<Ruta> obtenerTodas() {
    return new ArrayList<>(rutas.values());
  }

  public List<Ruta> obtenerRutasDelDia(LocalDate fecha) {
    return rutas.values().stream()
        .filter(r -> r.getFechaDeReparto().equals(fecha))
        .collect(Collectors.toList());
  }

  public List<Ruta> obtenerRutasEnCurso() {
    return rutas.values().stream()
        .filter(r -> r.getEstado() == EstadoRuta.EN_CURSO)
        .collect(Collectors.toList());
  }


  public Ruta buscarRutaPorEntregaId(Long idEntrega) {
    return rutas.values().stream()
        .filter(ruta -> ruta.getEntregas().stream().anyMatch( e -> e.getId().equals(idEntrega)))
        .findFirst()
        .orElse(null);
  }


}