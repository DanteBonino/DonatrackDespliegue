package Controllers.Web;

import io.javalin.http.Context;

public interface ConPaginacion {
  public static int CANTIDAD_DE_ELEMENTOS_POR_PAGINA = 10;

  public default int numeroDePagina(Context ctx) {
    try {
      return Integer.parseInt(ctx.queryParam("pagina"));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

}
