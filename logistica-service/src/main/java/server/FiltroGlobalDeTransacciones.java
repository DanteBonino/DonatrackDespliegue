package server;

import exceptions.TransaccionException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HandlerType;

public class FiltroGlobalDeTransacciones implements WithSimplePersistenceUnit {
  public void aplicarTransaccion(JavalinConfig config) {
    config.router.handlerWrapper(endpoint -> {
      if (endpoint.method.isHttpMethod() && !endpoint.method.equals(HandlerType.GET)) {
        return ctx -> withTransaction(() -> {
          try {
            endpoint.handler.handle(ctx);
          } catch (RuntimeException e) {
            throw e;
          } catch (Exception e) {
            throw new TransaccionException(e);
          }
          return null;
        });
      }
      return endpoint.handler;
    });
  }
}
