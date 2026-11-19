package Controllers.Web;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.io.InputStream;
import utils.FotoStorage;
import utils.Fotos;

public class FotosWebController {

  public FotosWebController() {
  }

  public void verFoto(Context ctx) {
    String nombre = ctx.pathParam("nombre");

    InputStream foto = Fotos.storage().abrir(nombre).orElseThrow(NotFoundResponse::new);

    ctx.contentType(Fotos.storage().contentTypeDe(nombre))
        .header("Cache-Control", "private, max-age=86400")
        .header("X-Content-Type-Options", "nosniff")
        .result(foto);
  }
}