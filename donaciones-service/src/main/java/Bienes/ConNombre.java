package Bienes;

public interface ConNombre {
  String getNombre();

  default Boolean seLlama(String nombre) {
    return this.getNombre().equals(nombre);
  }
}
