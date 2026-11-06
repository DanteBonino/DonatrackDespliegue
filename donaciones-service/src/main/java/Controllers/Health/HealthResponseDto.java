package Controllers.Health;

public record HealthResponseDto(
    String status,
    String timestamp,
    String uptime,
    String serviceName
) { }
