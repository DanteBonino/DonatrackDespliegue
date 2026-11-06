package repositories;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryEnMemoria;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de RutasRepository (Filtros)")
class RutasRepositoryTest {

  private RutasRepository repository;

  @BeforeEach
  void setUp() {
    repository = new RutasRepositoryEnMemoria();
  }

  @Test
  @DisplayName("obtenerRutasEnCurso() devuelve solo las rutas en estado EN_CURSO")
  void obtenerRutasEnCursoFiltraCorrectamente() {
    Camion camion1 = new Camion("AAA111", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
    Camion camion2 = new Camion("BBB222", 12.0, 3.5,
        6000.0, new Posicion(-34.59855526311623, -58.42013582713193));
    Camion camion3 = new Camion("CCC333", 15.0, 4.0,
        8000.0, new Posicion(-34.59855526311623, -58.42013582713193));

    Ruta rutaEnCurso = new Ruta(LocalDate.now());
    rutaEnCurso.agregarEntrega(FabricaDeDatos.nuevaEntrega("DON-1", "BEN-1"));
    camion1.asignarRuta(rutaEnCurso);
    camion1.iniciarRuta(); // Pasa a EN_CURSO

    Ruta rutaPlanificada = new Ruta( LocalDate.now());
    rutaPlanificada.agregarEntrega(FabricaDeDatos.nuevaEntrega( "DON-2", "BEN-2"));
    camion2.asignarRuta(rutaPlanificada);
    // Se queda en PLANIFICADA

    Ruta rutaFinalizada = new Ruta( LocalDate.now());
    rutaFinalizada.agregarEntrega(FabricaDeDatos.nuevaEntrega("DON-3", "BEN-3"));
    camion3.asignarRuta(rutaFinalizada);
    camion3.iniciarRuta();
    rutaFinalizada.getEntregas().get(0).confirmarRecepcion("foto.jpg", "CCC333");
    camion3.finalizarRuta(); // Pasa a FINALIZADA

    repository.registrar(rutaEnCurso);
    repository.registrar(rutaPlanificada);
    repository.registrar(rutaFinalizada);

    List<Ruta> rutasEnCurso = repository.obtenerRutasEnCurso();

    assertEquals(1, rutasEnCurso.size());
    assertEquals(rutaEnCurso.getNombre(), rutasEnCurso.get(0).getNombre());
    assertEquals(EstadoRuta.EN_CURSO, rutasEnCurso.get(0).getEstado());
  }

  @Test
  @DisplayName("obtenerRutasDelDia() filtra por fecha de reparto")
  void obtenerRutasDelDiaFiltraPorFecha() {
    Camion camion = new Camion("AAA111", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));

    Ruta rutaHoy = new Ruta( LocalDate.now());
    rutaHoy.agregarEntrega(FabricaDeDatos.nuevaEntrega( "DON-1", "BEN-1"));
    camion.asignarRuta(rutaHoy);

    Camion camionConRutaParaManiana = new Camion("BBB222", 12.0, 3.5,
        6000.0, new Posicion(-34.59855526311623, -58.42013582713193));

    Ruta rutaManana = new Ruta(LocalDate.now().plusDays(1));
    rutaManana.agregarEntrega(FabricaDeDatos.nuevaEntrega( "DON-2", "BEN-2"));
    camionConRutaParaManiana.asignarRuta(rutaManana);

    repository.registrar(rutaHoy);
    repository.registrar(rutaManana);

    List<Ruta> rutasHoy = repository.obtenerRutasDelDia(LocalDate.now());

    assertEquals(1, rutasHoy.size());
    assertEquals(rutaHoy.getNombre(), rutasHoy.get(0).getNombre());
  }
}