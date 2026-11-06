package controllers.utils;

import exceptions.AlmaceniamientoDeComprobanteException;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlmacenamientoDeComprobantes {
  private final Path directorioBase;

  public AlmacenamientoDeComprobantes(Path directorioBase) {
    this.directorioBase = directorioBase;
  }

  public String almacenarComprobante(UploadedFile archivo, Long idEntrega, String idDonacion, String patenteCamion) {
    String extension = extensionDe(archivo.filename());
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String nombreGuardado = String.format("%s_ENT-%d_DON-%s_%s%s",
        timestamp, idEntrega, idDonacion, patenteCamion, extension);
    try {
      Files.createDirectories(directorioBase);
      Path destino = directorioBase.resolve(nombreGuardado);
      try (InputStream inputStream = archivo.content()) {
        Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
      }
      return directorioBase.getFileName().toString() + "/" + nombreGuardado;
    } catch (IOException e) {
      throw new AlmaceniamientoDeComprobanteException("Error al guardar el comprobante en disco");
    }
  }

  private String extensionDe(String nombreOriginal) {
    int i = nombreOriginal.lastIndexOf('.');
    return i > 0 ? nombreOriginal.substring(i) : "";
  }
}
