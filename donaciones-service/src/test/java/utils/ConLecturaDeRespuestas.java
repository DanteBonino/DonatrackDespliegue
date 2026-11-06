package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;

public interface ConLecturaDeRespuestas {
  ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

  default <T> T leerComo(Response respuesta, Class<T> tipo) throws IOException {
    return OBJECT_MAPPER.readValue(respuesta.body().string(), tipo);
  }

  default <T> List<T> leerListaComo(Response respuesta, Class<T> tipoDeElemento) throws IOException {
    CollectionType tipoDeLista = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, tipoDeElemento);
    return OBJECT_MAPPER.readValue(respuesta.body().string(), tipoDeLista);
  }
}
