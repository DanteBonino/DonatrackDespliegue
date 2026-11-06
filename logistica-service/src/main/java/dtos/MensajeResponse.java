package dtos;

/**
 * Representa una respuesta que contiene un mensaje de texto.
 * <p>
 * Este DTO se utiliza para devolver información simple a un cliente,
 * normalmente en servicios REST o controladores de la capa de aplicación.
 * </p>
 *
 * @param mensaje el contenido textual de la respuesta
 */
public record MensajeResponse(String mensaje) {
}
