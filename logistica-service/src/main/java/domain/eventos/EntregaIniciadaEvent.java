package domain.eventos;

public record EntregaIniciadaEvent(
    String idEntrega,
    String idDonacion,
    String idEntidadBeneficiaria
) {}
