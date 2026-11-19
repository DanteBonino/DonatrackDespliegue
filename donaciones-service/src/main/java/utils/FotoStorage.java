package utils;

import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class FotoStorage {
  private static final Map<String, String> EXTENSIONES =
      Map.of("image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
  private static final Map<String, String> CONTENT_TYPES =
      Map.of("jpg", "image/jpeg", "png", "image/png", "webp", "image/webp");
  private static final Pattern NOMBRE_VALIDO = Pattern.compile("^[0-9a-f-]{36}\\.(jpg|png|webp)$");

  private final Path carpeta;

  public FotoStorage(Path carpeta) {
    this.carpeta = carpeta;
    try {
      Files.createDirectories(carpeta);
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo crear la carpeta de fotos: " + carpeta, e);
    }
    if (!Files.isWritable(carpeta)) {
      throw new IllegalStateException("La carpeta de fotos no es escribible: " + carpeta
          + " (revisá el volumen y los permisos del usuario del contenedor)");
    }
  }

  public static FotoStorage desdeEntorno() {
    String dir = System.getenv("DONATRACK_FOTOS_DIR");
    if (dir == null || dir.isBlank()) {
      throw new IllegalStateException("Falta la variable de entorno DONATRACK_FOTOS_DIR");
    }
    return new FotoStorage(Path.of(dir));
  }

  public List<String> guardar(List<UploadedFile> archivos) {
    List<Path> escritos = new ArrayList<>();
    try {
      for (UploadedFile a : archivos) {
        String extension = EXTENSIONES.get(a.contentType());
        if (extension == null) throw new IllegalArgumentException("Tipo de imagen no soportado");
        Path destino = carpeta.resolve(UUID.randomUUID() + extension);
        escritos.add(destino);   // se registra antes de copiar, así se limpia también una copia a medias
        try (InputStream in = a.content()) {
          Files.copy(in, destino);
        }
      }
      return escritos.stream().map(p -> p.getFileName().toString()).toList();
    } catch (IOException e) {
      escritos.forEach(this::borrar);
      throw new UncheckedIOException(e);
    } catch (RuntimeException e) {
      escritos.forEach(this::borrar);
      throw e;
    }
  }

  public void eliminar(List<String> nombres) {
    nombres.stream().filter(this::esNombreValido).forEach(n -> borrar(carpeta.resolve(n)));
  }

  public boolean esNombreValido(String nombre) {
    return nombre != null && NOMBRE_VALIDO.matcher(nombre).matches();
  }

  public Optional<InputStream> abrir(String nombre) {
    if (!esNombreValido(nombre)) return Optional.empty();
    Path archivo = carpeta.resolve(nombre);
    if (!Files.isRegularFile(archivo)) return Optional.empty();
    try {
      return Optional.of(Files.newInputStream(archivo));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String contentTypeDe(String nombre) {
    return CONTENT_TYPES.get(nombre.substring(nombre.lastIndexOf('.') + 1));
  }

  private void borrar(Path p) {
    try { Files.deleteIfExists(p); } catch (IOException ignorada) { }
  }
}