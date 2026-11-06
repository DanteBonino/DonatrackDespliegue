package dtos;

public record MonitoreoRutaDto(
  String nombreRuta,
  String patenteCamion,
  double latitud,
  double longitud,
  String estadoRuta,
  int totalEntregas,
  int entregasCompletadas
  ) {}
