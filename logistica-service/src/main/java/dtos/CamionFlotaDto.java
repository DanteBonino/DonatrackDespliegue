package dtos;

public record CamionFlotaDto(
    String patente,
    double volumen,
    double altura,
    double capacidad,
    String estado
) {}
