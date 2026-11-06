package datosmock;

import domain.Camion;
import domain.Entrega;
import domain.Posicion;
import domain.Ruta;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;
import config.Configuracion;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import utility.FabricaDeDatos;

public class MockDataLoader implements WithSimplePersistenceUnit {
  public static void cargar(Posicion posicionDelDeposito) {
    FlotaRepositoryConPersistencia flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    RutasRepositoryConPersistencia rutasRepository = RutasRepositoryConPersistencia.getInstance();
    // =====================================================================
    // ESCENARIO 1: Camion DISPONIBLE (Sin ruta asignada)
    // =====================================================================
    Camion camionDisponible = new Camion("AAA111", 10.0, 3.0,
        5000.0, posicionDelDeposito);


    // =====================================================================
    // ESCENARIO 2: Camion con ruta PLANIFICADA para MANANA
    // =====================================================================
    Camion camionPlanificado = new Camion("BBB222", 12.0, 3.5,
        6000.0, posicionDelDeposito);
    Ruta rutaPlanificada = new Ruta( LocalDate.now().plusDays(1));
    Entrega entrega1 = FabricaDeDatos.nuevaEntrega("DON-201", "ENT-BEN-01");
    Entrega entrega2 = FabricaDeDatos.nuevaEntrega( "DON-202", "ENT-BEN-02");
    rutaPlanificada.agregarEntrega(entrega1);
    rutaPlanificada.agregarEntrega(entrega2);
    camionPlanificado.asignarRuta(rutaPlanificada);


    // =====================================================================
    // ESCENARIO 3: Camion con ruta PLANIFICADA para HOY
    // (Ideal para probar POST /iniciar-ruta sin cambiar fechas)
    // =====================================================================
    Camion camionPlanificadoHoy = new Camion("HHH888", 14.0, 3.8,
        7000.0, posicionDelDeposito);
    Ruta rutaPlanificadaHoy = new Ruta(LocalDate.now());
    Entrega entregaHoy1 = FabricaDeDatos.nuevaEntrega( "DON-801", "ENT-BEN-20");
    Entrega entregaHoy2 = FabricaDeDatos.nuevaEntrega("DON-802", "ENT-BEN-21");
    rutaPlanificadaHoy.agregarEntrega(entregaHoy1);
    rutaPlanificadaHoy.agregarEntrega(entregaHoy2);
    camionPlanificadoHoy.asignarRuta(rutaPlanificadaHoy);


    // =====================================================================
    // ESCENARIO 4: Camion con ruta EN_CURSO (Está en el obelisco)
    // =====================================================================
    Camion camionEnCurso = new Camion("CCC333", 15.0, 4.0,
        8000.0, posicionDelDeposito);
    Ruta rutaEnCurso = new Ruta( LocalDate.now());
    Entrega entrega3 = FabricaDeDatos.nuevaEntrega( "DON-301", "ENT-BEN-03");
    Entrega entrega4 = FabricaDeDatos.nuevaEntrega( "DON-302", "ENT-BEN-04");

    rutaEnCurso.agregarEntrega(entrega3);
    rutaEnCurso.agregarEntrega(entrega4);
    camionEnCurso.asignarRuta(rutaEnCurso);
    camionEnCurso.iniciarRuta(); // Pasa ruta a EN_CURSO y entregas a EN_CAMINO
    camionEnCurso.actualizarPosicion(new Posicion(-34.6037, -58.3816)); // Obelisco


    // =====================================================================
    // ESCENARIO 5: Camion EN_CURSO con PROGRESO PARCIAL (Entregas mixtas)
    // 1 Completada, 1 en Incidencia, 1 En Camino
    // =====================================================================
    Camion camionMixto = new Camion("EEE555", 12.0, 3.5,
        6500.0, posicionDelDeposito);
    Ruta rutaMixta = new Ruta( LocalDate.now());

    Entrega entregaCompletada = FabricaDeDatos.nuevaEntrega( "DON-501", "ENT-BEN-10");
    Entrega entregaIncidencia = FabricaDeDatos.nuevaEntrega("DON-502", "ENT-BEN-11");
    Entrega entregaEnCamino = FabricaDeDatos.nuevaEntrega( "DON-503", "ENT-BEN-12");

    rutaMixta.agregarEntrega(entregaCompletada);
    rutaMixta.agregarEntrega(entregaIncidencia);
    rutaMixta.agregarEntrega(entregaEnCamino);
    camionMixto.asignarRuta(rutaMixta);
    camionMixto.iniciarRuta();

    // Simulamos el progreso del chofer
    entregaCompletada.confirmarRecepcion("foto_ok_501.jpg", "EEE555");
    entregaIncidencia.reportarIncidencia("Entidad beneficiaria cerrada");
    // entregaEnCamino se queda en EN_CAMINO

    camionMixto.actualizarPosicion(new Posicion(-34.6150, -58.3700)); // San Telmo (Defensa y México)


    // =====================================================================
    // ESCENARIO 6: Camion con ruta FINALIZADA (Volvio al deposito) --> No tenía sentido se iniciaba una ruta con fecha de recorrido viejo
    // =====================================================================
//    Camion camionFinalizado = new Camion("DDD444", 10.0, 3.0,
//        5000.0, posicionDelDeposito);
//    Ruta rutaFinalizada = new Ruta("R-004", LocalDate.now().minusDays(1));
//    Entrega entrega5 = new Entrega("ENT-005", "DON-401", "ENT-BEN-05");
//    rutaFinalizada.agregarEntrega(entrega5);
//    rutaFinalizada.asignarCamion(camionFinalizado);
//    rutaFinalizada.darInicio();
//    entrega5.confirmarRecepcion("comprobante_ok.jpg", "DDD444");
//    rutaFinalizada.verificarFinalizacion();
//    rutasRepository.registrar(rutaFinalizada);
//    flotaRepositoryEnMemoria.registrarCamion(camionFinalizado);

    // =====================================================================
    // ESCENARIO 7 y 8: Camiones ADICIONALES DISPONIBLES
    // (Util para que el cronjob tenga flota suficiente para planificar)
    // =====================================================================
    Camion camionDisponible2 = new Camion("FFF666", 18.0, 4.2,
        9000.0, posicionDelDeposito);
    Camion camionDisponible3 = new Camion("GGG777", 8.0, 2.8,
        4000.0, posicionDelDeposito);


    new MockDataLoader().withTransaction(() -> {
      flotaRepository.registrar(camionDisponible);
      rutasRepository.registrar(rutaPlanificada);
      flotaRepository.registrar(camionPlanificado);
      rutasRepository.registrar(rutaPlanificadaHoy);
      flotaRepository.registrar(camionPlanificadoHoy);
      rutasRepository.registrar(rutaEnCurso);
      flotaRepository.registrar(camionEnCurso);
      rutasRepository.registrar(rutaMixta);
      flotaRepository.registrar(camionMixto);
      flotaRepository.registrar(camionDisponible2);
      flotaRepository.registrar(camionDisponible3);
    });
    System.out.println("Terminé de cargar");
  }
}