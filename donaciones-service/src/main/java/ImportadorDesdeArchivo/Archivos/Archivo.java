package ImportadorDesdeArchivo.Archivos;


import DTOs.DonanteDto;
import java.util.List;

public abstract class Archivo {
  protected String path;

  public Archivo(String path) {
    this.path = path;
  }

  abstract public List<DonanteDto> importarInstancias();

}
