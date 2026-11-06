package Controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Bienes.SubCategoria.SubCategoria;
import Controllers.SubCategorias.SubCategoriaDto;
import Injector.Injector;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeSubCategorias;

@ExtendWith(MockitoExtension.class)
public class SubCategoriasControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {
  private SubCategoriasRepository subCategoriasRepository;
  private CategoriasRepository categoriasRepository;
  @Mock
  private Injector inyectorDeDependencias;
  private Javalin app;

  @BeforeEach
  void setUp() {
    subCategoriasRepository = SubCategoriasRepository.getInstance();
    categoriasRepository = CategoriasRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }

  private String pathBase() {
    return "/subcategorias";
  }

  @Nested
  class SinSubCategoriasPersistidas {
    @Test
    void pedirTodasDevuelveUnaListaVaciaY200ComoStatusCode() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(leerRespuestas(respuestaHttp)).isEmpty();
      });
    }
  }
  @Nested
  class ConSubCategoriasPersistidas {
    private SubCategoria remera;
    @BeforeEach
    void setUp() {
      remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
      withTransaction(() -> {
        categoriasRepository.registrar(remera.getCategoria());
        subCategoriasRepository.registrar(remera);
      });
    }

    @Test
    void pedirTodasDevuelveUnaStatusCode200() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertThat(respuestaHttp.code()).isEqualTo(200);
      });
    }

    @Test
    void pedirTodasSiHayUnaSolaPersistidaDevuelveUnaListaDe1Elemento() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertEquals(1, leerRespuestas(respuestaHttp).size());
      });
    }

    @Test
    void pedirTodasSiHayUnaSolaPersistidaDevuelveUnaListaDe1ElementoCuyoNombreCoincideConElDelElementoPersistido() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        SubCategoriaDto subcategoriaEncontrada = leerRespuestas(respuestaHttp).get(0);
        assertEquals(remera.getNombre(), subcategoriaEncontrada.getNombre());
      });
    }



  }

  private List<SubCategoriaDto> leerRespuestas(Response respuesta)  throws IOException {
    return leerListaComo(respuesta, SubCategoriaDto.class);
  }
}
