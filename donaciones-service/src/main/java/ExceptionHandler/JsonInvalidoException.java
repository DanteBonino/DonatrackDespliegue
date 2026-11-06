package ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonInvalidoException extends RuntimeException {
  public JsonInvalidoException(JsonProcessingException causa) {
    super(causa.getOriginalMessage(), causa);
  }
}
