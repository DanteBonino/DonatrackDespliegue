package ObserversCambioDeEstado.EntregaConExito;

import java.time.LocalDateTime;

public record EntregaConExitoDTO(String idDonacion, String idEntidad, LocalDateTime timestamps, String patenteCamion) {
}
