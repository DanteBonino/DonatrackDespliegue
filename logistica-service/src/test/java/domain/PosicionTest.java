package domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de Posicion (Validación de Coordenadas)")
class PosicionTest {

  @Test
  @DisplayName("Crea una posición válida con coordenadas dentro del rango")
  void creaPosicionValida() {
    assertDoesNotThrow(() -> new Posicion(-34.6, -58.4));
  }

  @Test
  @DisplayName("Lanza IllegalArgumentException si la latitud es mayor a 90")
  void latitudMayorA90LanzaExcepcion() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> new Posicion(91.0, -58.4));
    assertTrue(exception.getMessage().contains("Latitud"));
  }

  @Test
  @DisplayName("Lanza IllegalArgumentException si la latitud es menor a -90")
  void latitudMenorAMenos90LanzaExcepcion() {
    assertThrows(IllegalArgumentException.class,
        () -> new Posicion(-91.0, -58.4));
  }

  @Test
  @DisplayName("Lanza IllegalArgumentException si la longitud es mayor a 180")
  void longitudMayorA180LanzaExcepcion() {
    assertThrows(IllegalArgumentException.class,
        () -> new Posicion(-34.6, 181.0));
  }

  @Test
  @DisplayName("Lanza IllegalArgumentException si la longitud es menor a -180")
  void longitudMenorAMenos180LanzaExcepcion() {
    assertThrows(IllegalArgumentException.class,
        () -> new Posicion(-34.6, -181.0));
  }

  @Test
  @DisplayName("Acepta coordenadas en los límites exactos (90, -90, 180, -180)")
  void aceptaCoordenadasEnLosLimites() {
    assertDoesNotThrow(() -> new Posicion(90.0, 180.0));
    assertDoesNotThrow(() -> new Posicion(-90.0, -180.0));
  }
}