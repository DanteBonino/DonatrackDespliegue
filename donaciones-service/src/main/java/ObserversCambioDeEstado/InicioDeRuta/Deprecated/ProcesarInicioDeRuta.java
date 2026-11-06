package ObserversCambioDeEstado.InicioDeRuta.Deprecated;

//import Donaciones.DonacionIndependiente;
//import EntidadesBeneficiarias.EntidadBeneficiaria;
//import EstadoDonacionImpl.EnTraslado;
//import ExceptionHandler.NoSeEncontroEnElRepositoryException;
//import InicioDeRuta.DonacionEntidadDTO;
//import InicioDeRuta.PublicadorDeInicioDeRuta;
//import Repositories.BeneficiariosRepository;
//import Repositories.DonacionesRepository;
//import java.util.List;
//
//public class ProcesarInicioDeRuta {
//  private BeneficiariosRepository beneficiariosRepository;
//  private DonacionesRepository donacionesRepository;
//  private PublicadorDeInicioDeRuta publicadorDeInicioDeRuta;
//
//  public ProcesarInicioDeRuta(BeneficiariosRepository beneficiariosRepository, DonacionesRepository donacionesRepository, PublicadorDeInicioDeRuta publicadorDeInicioDeRuta) {
//    this.beneficiariosRepository = beneficiariosRepository;
//    this.donacionesRepository = donacionesRepository;
//    this.publicadorDeInicioDeRuta = publicadorDeInicioDeRuta;
//  }
//
//  public void enviarAviso (List<DonacionEntidadDTO> donacionEntidads) {
//    donacionEntidads.forEach(par ->
//    {
//      try {
//        DonacionIndependiente donacionIndependiente = this.donacionesRepository.buscarPorId(par.idDonacion()).orElseThrow(()-> new NoSeEncontroEnElRepositoryException(par.idDonacion()));
//        EntidadBeneficiaria entidadBeneficiaria = this.beneficiariosRepository.buscarPorId(par.idEntidad()).orElseThrow(()-> new NoSeEncontroEnElRepositoryException(par.idEntidad()));
//        donacionIndependiente.cambiarEstado(new EnTraslado());
//        this.publicadorDeInicioDeRuta.publicarInicioDeRuta(donacionIndependiente, entidadBeneficiaria);
//      } catch (RuntimeException error) {
//        System.out.println(error);
//      }
//    }
//    );
//  }
//}
