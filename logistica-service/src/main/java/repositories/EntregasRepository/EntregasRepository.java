package repositories.EntregasRepository;

import domain.Entrega;

public interface EntregasRepository {
  Entrega obtenerPorId(Long entregaId);

  Entrega obtenerPorDonacionId(String donacionId);

  Entrega obtenerPorNombre(String nombre);
}
