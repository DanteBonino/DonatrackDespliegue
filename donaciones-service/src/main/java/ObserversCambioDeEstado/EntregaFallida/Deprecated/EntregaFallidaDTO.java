package ObserversCambioDeEstado.EntregaFallida.Deprecated;

//public record EntregaFallidaDTO(String idDonacion, String idEntidad, String justificacion) {
//  public static class ProcesarEntregaFallida {
//    DonacionesRepository donacionesRepository;
//    BeneficiariosRepository beneficiariosRepository;
//    PublicadorEntregaFallida publicadorEntregaFallida;
//
//    public ProcesarEntregaFallida(DonacionesRepository donacionesRepository, BeneficiariosRepository beneficiariosRepository, PublicadorEntregaFallida publicadorEntregaFallida) {
//      this.donacionesRepository = donacionesRepository;
//      this.beneficiariosRepository = beneficiariosRepository;
//      this.publicadorEntregaFallida = publicadorEntregaFallida;
//    }
//
//    public void publicar(EntregaFallidaDTO entregaFallidaDTO) {
//      DonacionIndependiente donacionIndependiente =
//          this.donacionesRepository.buscarPorId(entregaFallidaDTO.idDonacion()).orElseThrow(() -> new NoSeEncontroEnElRepositoryException(entregaFallidaDTO.idDonacion()));
//      EntidadBeneficiaria entidadBeneficiaria =
//          this.beneficiariosRepository.buscarPorId(entregaFallidaDTO.idEntidad()).orElseThrow(() -> new NoSeEncontroEnElRepositoryException(entregaFallidaDTO.idEntidad()));
//      donacionIndependiente.cambiarEstado(new EntregaFallida(entregaFallidaDTO.justificacion()));
//      this.publicadorEntregaFallida.publicarEntregaFallida(entregaFallidaDTO, entidadBeneficiaria, donacionIndependiente);
//    }
//  }
//}
