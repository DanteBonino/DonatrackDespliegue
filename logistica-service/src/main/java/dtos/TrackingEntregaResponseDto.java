package dtos;

public record TrackingEntregaResponseDto(
   String idDonacion,
   String estadoEntrega,
   String patenteCamion,
   Double latitud,
   Double longitud,
   Double velocidad,
   String urlGoogleMaps
) {}
