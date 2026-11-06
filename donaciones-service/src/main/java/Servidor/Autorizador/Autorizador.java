package Servidor.Autorizador;

import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import UsuarioFinal.Rol;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Autorizador {
  public static void autorizar(Context ctx) {
    var permittedRoles = ctx.routeRoles();

    if (permittedRoles.contains(Rol.CUALQUIERA) ||
        permittedRoles.isEmpty() ||
        (permittedRoles.contains(Rol.SINSESIONACTIVA) && !ManejadorDeSesion.haySesion(ctx)) ||
        ManejadorDeSesion.obtenerRol(ctx).stream().anyMatch(permittedRoles::contains) ||
        permittedRoles.contains(Rol.CONSESIONACTIVA) && ManejadorDeSesion.haySesion(ctx)
    ) {
      return;
    }

    if (permittedRoles.contains(Rol.SINSESIONACTIVA) && ManejadorDeSesion.haySesion(ctx)) {
      ctx.redirect("/");
      return;
    }

    if (ManejadorDeSesion.obtenerRol(ctx).isEmpty()) {
      ctx.redirect("/login");
      return;
    }

    if (ManejadorDeSesion.obtenerRol(ctx).stream().noneMatch(permittedRoles::contains)) {
      ctx.redirect("/dashboard");
    }
  }
}
