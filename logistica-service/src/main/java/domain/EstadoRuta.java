package domain;

import javax.persistence.Enumerated;

/**
 * Estados posibles de una ruta dentro del sistema logistico.
 *
 * <p>Convenciones:
 * - PLANIFICADA: La ruta fue creada por el planificador, pero el chofer no dio inicio.
 * - EN_CURSO: El chofer indico en el sistema que da inicio a su ruta.
 * - FINALIZADA: Todas las entregas se completan o estan en incidencia.
 */
public enum   EstadoRuta {
  PLANIFICADA,
  EN_CURSO,
  FINALIZADA,
  DISPONIBLE
}
