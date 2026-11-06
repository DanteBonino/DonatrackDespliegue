package Controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Controllers.Health.HealthResponseDto;
import Controllers.SubCategorias.SubCategoriaDto;
import Injector.Injector;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;

@ExtendWith(MockitoExtension.class)
public class HealthControllerTest implements ConLecturaDeRespuestas {
  private Javalin app;
  @Mock
  private Injector inyectorDeDependencias;

  @BeforeEach
  void setUp() {
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }

  @Test
  void healthDevuelve200() {
    JavalinTest.test(app, (server, client) -> {
      Response respuesta = client.get("/health");
      assertThat(respuesta.code()).isEqualTo(200);
    });
  }

  @Test
  void healthInformaEstadoUpYNombreDelServicio() {
    JavalinTest.test(app, (server, client) -> {
      HealthResponseDto dto = leerComo(client.get("/health"), HealthResponseDto.class);
      assertEquals("UP", dto.status());
      assertEquals("donaciones-service", dto.serviceName());
    });
  }

  private HealthResponseDto leer(Response respuesta)  throws IOException {
    return leerComo(respuesta, HealthResponseDto.class);
  }

}
