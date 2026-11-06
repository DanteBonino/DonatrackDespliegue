package Controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Bienes.Categoria.Categoria;
import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.Categoria.CategoriaDto;
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
import utils.FabricaDeCategorias;
import utils.FabricaDeSubCategorias;

@ExtendWith(MockitoExtension.class)
public class CategoriasControllerTest  implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {
    private CategoriasRepository categoriasRepository;
    @Mock
    private Injector inyectorDeDependencias;
    private Javalin app;

    @BeforeEach
    void setUp() {
      categoriasRepository = CategoriasRepository.getInstance();
      app = ServidorDonaciones.crearApp(inyectorDeDependencias);
    }

    private String pathBase() {
      return "/categorias";
    }

    @Nested
    class SinCategoriasPersistidas {
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
    class ConCategoriasPersistidas {
      private Categoria vestimenta;
      @BeforeEach
      void setUp() {
        vestimenta = FabricaDeCategorias.getCategoriaByNombreConExcepcion("VESTIMENTA");
        withTransaction(() -> {
          categoriasRepository.registrar(vestimenta);
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
          CategoriaDto categoriaEncontrada = leerRespuestas(respuestaHttp).get(0);
          assertEquals(vestimenta.getNombre(), categoriaEncontrada.getNombre());
        });
      }



    }

    private List<CategoriaDto> leerRespuestas(Response respuesta)  throws IOException {
      return leerListaComo(respuesta, CategoriaDto.class);
    }
}

