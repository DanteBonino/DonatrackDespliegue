package Servidor.ManejadorDeSesiones;

import UsuarioFinal.Rol;
import io.javalin.http.Context;
import java.util.Optional;

public class ManejadorDeSesion {
  private static final String KEY = "datosSesion";

  public static Optional<String> obtenerIdAsoaciado(Context ctx) {
    return obtenerSesion(ctx).map(DatosSesion::idAsociado);
  }

  public static Optional<DatosSesion> obtenerSesion(Context ctx) {
    return Optional.ofNullable(ctx.sessionAttribute(KEY));
  }

  public static Optional<Rol> obtenerRol(Context ctx) {
    return obtenerSesion(ctx).map(DatosSesion::rol);
  }

  public static Optional<String> obtenerNombreMostrable(Context ctx) {
    return obtenerSesion(ctx).map(DatosSesion::nombreMostrable);
  }

  public static boolean haySesion(Context ctx) {
    return obtenerSesion(ctx).isPresent();
  }

  public static void iniciarSesion(Context ctx, DatosSesion datosSesion) {
    ctx.sessionAttribute(KEY, datosSesion);
  }

  public static void cerrarSesion(Context ctx) {
    ctx.req().getSession().invalidate();
  }
}
