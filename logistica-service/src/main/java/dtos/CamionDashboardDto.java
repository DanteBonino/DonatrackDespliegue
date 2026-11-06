package dtos;

/**
 * Contiene información resumida del camión,
 * su posición actual (GPS), y
 * el estado/progreso de su ruta asignada.
 */
public record CamionDashboardDto(
    String patente,
    double latitud,
    double longitud,
    String estadoRuta,      // "DISPONIBLE", "PLANIFICADA", "EN_CURSO", "FINALIZADA"
    String nombreRutaAsignada,
    int totalEntregas,
    int entregasCompletadas
) {}