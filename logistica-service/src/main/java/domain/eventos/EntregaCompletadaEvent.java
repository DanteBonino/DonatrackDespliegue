package domain.eventos;

public record EntregaCompletadaEvent(
    String idEntrega,
    String idDonacion,
    String idEntidadBeneficiaria,
    String comprobanteFoto,
    String patenteCamion
) {}
