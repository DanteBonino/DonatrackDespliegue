package dtos;

public record HealthResponse(
    String status,
    String timestamp,
    String uptime,
    String serviceName
) {}
