package dtos;

/**
 * Representa la respuesta con informacion del camion.
 *
 * @param patente      patente del camión
 * @param latitud      latitud actual en grados decimales (WGS84)
 * @param longitud     longitud actual en grados decimales (WGS84)
 * @param estadoRuta   estado de la ruta asignada (ej. "EN_CURSO", "FINALIZADA")
 * @param idRutaActual identificador unico de la ruta actual, si existe
 */
public record CamionResponseDto(
    String patente,
    double latitud,
    double longitud,
    String estadoRuta,
    String nombreRutaAsignada
) {}
