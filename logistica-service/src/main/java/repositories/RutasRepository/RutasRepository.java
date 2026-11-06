package repositories.RutasRepository;

import domain.Entrega;
import domain.Ruta;
import java.time.LocalDate;
import java.util.List;

public interface RutasRepository {
  void registrar(Ruta ruta);

  List<Ruta> obtenerTodas();

  List<Ruta> obtenerRutasDelDia(LocalDate fecha);

  List<Ruta> obtenerRutasEnCurso();

  Ruta buscarRutaPorEntregaId(Long idEntrega);
}
