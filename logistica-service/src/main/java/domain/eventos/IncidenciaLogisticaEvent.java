package domain.eventos;

public record IncidenciaLogisticaEvent(
   String idEntrega,
   String idDonacion,
   String idEntidadBeneficiaria,
   String motivoNoRecepcion
) {}
