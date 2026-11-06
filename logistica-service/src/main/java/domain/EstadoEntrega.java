package domain;

/**
 * Estados posibles de una entrega dentro del sistema logistico.
 *
 * <p>Convenciones:
 * - PLANIFICADA: La ruta fue planificada pero el camion aun no sale.
 * - EN_CAMINO: El chofer dio "inicio de ruta" en el sistema.
 * - COMPLETADA: La entidad confirmo la recepcion de la entrega.
 * - INCIDENCIA: La entidad reporto que no llego la entrega.
 */
public enum EstadoEntrega {
  PLANIFICADA,
  EN_CAMINO,
  COMPLETADA,
  INCIDENCIA
}
