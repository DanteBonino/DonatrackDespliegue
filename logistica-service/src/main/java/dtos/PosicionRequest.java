package dtos;

/**
 * Representa una solicitud de posición geográfica.
 * <p>
 * Los valores de latitud y longitud se expresan en grados decimales (DD),
 * utilizando el sistema de referencia WGS84.
 * </p>
 *
 * <ul>
 *   <li>Latitud: -90 a +90 (N positivo, S negativo).</li>
 *   <li>Longitud: -180 a +180 (E positivo, O negativo).</li>
 *   <li>Velocidad: kilómetros por hora (km/h).</li>
 * </ul>
 *
 * Este DTO se utiliza para transportar datos de entrada en servicios REST
 * o controladores de la capa de aplicación.
 *
 * @param latitud   la latitud en grados decimales (WGS84)
 * @param longitud  la longitud en grados decimales (WGS84)
 * @param velocidad la velocidad en kilómetros por hora
 */
public record PosicionRequest(double latitud, double longitud, double velocidad) {
}
