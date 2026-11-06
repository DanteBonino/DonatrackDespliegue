package Actualizable;

public interface Actualizable<D> {
  default void refrescarParcial(D datos){
    actualizarDesde(datos, true);
  };

  void actualizarDesde(D datos, boolean pacial);

}