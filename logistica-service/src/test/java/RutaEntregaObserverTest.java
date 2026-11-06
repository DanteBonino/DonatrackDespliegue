import domain.Posicion;
import domain.Ruta;
import domain.Camion;
import domain.Entrega;

import domain.eventos.EntregaObserver;
import domain.eventos.EntregaPublicador;
import domain.eventos.EntregaIniciadaEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.mockito.Mockito.*;
import static utility.FabricaDeDatos.unaDireccionCualquiera;

@DisplayName("Tests de Integración: Ruta -> Entrega -> Observer")
public class RutaEntregaObserverTest {

  private Ruta ruta;
  private Camion camion;
  private EntregaPublicador publicador;
  private EntregaObserver observerMock;

  @BeforeEach
  void setUp() {
    ruta = new Ruta( LocalDate.now());
    camion = new Camion("ABC123", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));

    publicador = new EntregaPublicador();
    observerMock = mock(EntregaObserver.class);
    publicador.suscribir(observerMock);
  }

  @Test
  @DisplayName("Al iniciar la Ruta, las entregas asociadas disparan sus eventos de inicio")
  void alIniciarRutaLasEntregasNotificanAlObserver() {
    // 1. Creamos entregas reales (no mocks) y les inyectamos el publicador
    Entrega entrega1 = new Entrega("DON-1", "DEST-1", unaDireccionCualquiera(),publicador);

    Entrega entrega2 = new Entrega( "DON-2", "DEST-2",unaDireccionCualquiera(), publicador);

    // 2. Armamos la ruta
    camion.asignarRuta(ruta);
    ruta.agregarEntrega(entrega1);
    ruta.agregarEntrega(entrega2);

    // 3. El chofer inicia la ruta
    camion.iniciarRuta();

    // 4. Verificamos que el Observer recibió los eventos de AMBAS entregas
    verify(observerMock).anteEntregaIniciada(argThat(e -> e.idEntrega().equals(entrega1.getNombre())));
    verify(observerMock).anteEntregaIniciada(argThat(e -> e.idEntrega().equals(entrega1.getNombre())));
    verify(observerMock, times(2)).anteEntregaIniciada(any());
  }
}