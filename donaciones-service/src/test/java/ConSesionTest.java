

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import Donantes.Donante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConSesionTest {
  private Donante dante;

  @BeforeEach
  void setUp(){
    dante = mock(Donante.class, CALLS_REAL_METHODS);
  }

  @Test
  void unUsuarioQueNoSeConectaHace20DiasEsInactivo(){
    doReturn(20).when(dante).getCantidadDeDiasInactivo();
    assertTrue(dante.esInactivo());
  }

  @Test
  void unUsuarioCuyaConexionFueHaceMenosDe20DiasNoEsInactivo(){
    doReturn(19).when(dante).getCantidadDeDiasInactivo();
    assertFalse(dante.esInactivo());
  }

}