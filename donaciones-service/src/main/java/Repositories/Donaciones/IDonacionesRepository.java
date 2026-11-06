package Repositories.Donaciones;

import Donaciones.DonacionIndependiente;
import EstadoDonacion.EstadoDonacion;
import java.util.List;

public interface IDonacionesRepository {
  List<DonacionIndependiente> getInstancias();

  void registrar(DonacionIndependiente unaDonacion);

  DonacionIndependiente findById(String id);

  void eliminarInstancia(String id);

  
  default List<DonacionIndependiente> getListasParaEntregar() {
    return getEnEstado(EstadoDonacion.LISTA_PARA_ENTREGAR);
  }

  default List<DonacionIndependiente> getAsignadas() {
    return getEnEstado(EstadoDonacion.ASIGNACION_REALIZADA);
  }
  
  default  List<DonacionIndependiente> getPendientesDeRevision() {
    return getEnEstado(EstadoDonacion.ENTREGA_FALLIDA);
  }

  default List<DonacionIndependiente> getDonacionesEnDeposito() {
    return getEnEstado(EstadoDonacion.EN_DEPOSITO);
  }

  List<DonacionIndependiente> getEnEstado(EstadoDonacion unEstado);

}
