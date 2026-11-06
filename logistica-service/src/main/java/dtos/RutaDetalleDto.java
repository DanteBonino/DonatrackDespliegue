package dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RutaDetalleDto(
    String idRuta,
    LocalDate fechaDeReparto,
    String estado,
    String patenteCamion,
    List<EntregaDetalleDto> entregas
) {}
