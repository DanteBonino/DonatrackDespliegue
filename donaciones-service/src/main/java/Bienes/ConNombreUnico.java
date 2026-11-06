package Bienes;

public interface ConNombreUnico extends ConNombre {
  default boolean esLaMisma(ConNombreUnico algoConNombre) {
    return this.seLlama(algoConNombre.getNombre());
  }
}
