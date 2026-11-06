package domain.eventos;

public record EntregaReprogramadaEvent(
    String idEntrega,
    String idDonacion
) {}
