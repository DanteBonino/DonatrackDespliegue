package dtos;

public record CamionRequest(
    String patente,
    double volumen,
    double altura,
    double capacidad
) {}
