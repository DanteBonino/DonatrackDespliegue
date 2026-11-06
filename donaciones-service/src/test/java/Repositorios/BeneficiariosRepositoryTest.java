package Repositorios;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import Contacto.DatosDeContactoBuilder;
import Controllers.EntidadesBenefeciarias.DatosEntidadesBeneficiarias;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import ExceptionHandler.EntidadYaExisteException;
import ExceptionHandler.RecursoNoEncontradoException;
import MedioDeNotificacion.TipoDeMedio;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Beneficiarios.IBeneficiariosRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;

public class BeneficiariosRepositoryTest implements ConLimpiezaDeBaseDeDatos {

  private IBeneficiariosRepository repository;

  @BeforeEach
  void setUp() {
    repository = BeneficiariosRepository.getInstance();
  }

  private EntidadBeneficiaria unaEntidadDePrueba() {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of())
        .build();
  }

  private record DatosDePrueba(String razonSocial, String direccion, List<String> mails, String telefono, TipoDeMedio medio)
      implements DatosEntidadesBeneficiarias {
    @Override public String getRazonSocial() { return razonSocial; }
    @Override public List<String> getMails() { return mails; }
    @Override public String getTelefono() { return telefono; }
    @Override public TipoDeMedio getTipoDeMedio() {
      return medio;
    }
  }

  @Test
  void sePuedeRegistrarUnaEntidadBeneficiaria() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();

    withTransaction(()-> {
      repository.registrar(entidad);
    });

    assertThat(repository.getInstancias().size()).isEqualTo(1);
  }

  @Test
  void noSePuedeRegistrarDosEntidadesConLaMismaRazonSocial() {
    withTransaction(()-> {
      repository.registrar(unaEntidadDePrueba());
    });

    EntidadBeneficiaria duplicada = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("111")
            .conMail("otro@gmail.com")
            .build()
        )
        .conNecesidades(List.of())
        .build();

    assertThatThrownBy(() -> repository.registrar(duplicada))
        .isInstanceOf(EntidadYaExisteException.class);

    assertThat(repository.getInstancias()).hasSize(1);
  }

  @Test
  void sePuedenRegistrarEntidadesConRazonesSocialesDistintas() {
    withTransaction(()-> {
      repository.registrar(unaEntidadDePrueba());
    });

    EntidadBeneficiaria otra = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Fundación Manos Abiertas")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("222")
            .conMail("contacto@manosabiertas.org")
            .build()
        )
        .conNecesidades(List.of())
        .build();

    withTransaction(()-> {
      repository.registrar(otra);
    });

    assertThat(repository.getInstancias()).hasSize(2);
  }

  @Test
  void sePuedeEncontrarUnaEntidadPorId() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();
    withTransaction(() -> {
      repository.registrar(entidad);
    });

    assertThat(repository.findById(entidad.getIdDominio())).isSameAs(entidad);
  }

  @Test
  void siElIdNoExisteFindByIdLanzaExcepcion() {
    assertThatThrownBy(() -> repository.findById("id-inexistente"))
        .isInstanceOf(RecursoNoEncontradoException.class);
  }

  @Test
  void sePuedeEliminarUnaEntidadExistente() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();
    withTransaction(()-> {
      repository.registrar(entidad);
    });
    assertThat(repository.getInstancias().size()).isEqualTo(1);

    withTransaction(() -> {
      repository.eliminarInstancia(entidad.getIdDominio());
    });

    assertThat(repository.getInstancias()).isEmpty();
  }

  @Test
  void siElIdNoExisteEliminarInstanciaLanzaExcepcion() {
    assertThatThrownBy(() -> repository.eliminarInstancia("id-inexistente"))
        .isInstanceOf(RecursoNoEncontradoException.class);
  }

  @Test
  void sePuedeActualizarParcialmenteSoloElTelefono() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();

    withTransaction(() -> {
      repository.registrar(entidad);
    });

    withTransaction(() -> {
      repository.actualizarInstancia(entidad.getIdDominio(),
          new DatosDePrueba(null, null, null, "1198765432", null));
    });
    EntidadBeneficiaria actualizada = repository.findById(entidad.getIdDominio());
    assertThat(actualizada.getNumeroBase()).isEqualTo("1198765432");
    assertTrue(actualizada.getDireccion().esLaMismaQue(direccionCualquiera()));
    assertThat(actualizada.getRazonSocial()).isEqualTo("Comedor Los Pibes");
  }

  @Test
  void sePuedeCorregirLaRazonSocial() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();
    withTransaction(() -> {
      repository.registrar(entidad);
    });
    withTransaction(() -> {
      repository.actualizarInstancia(entidad.getIdDominio(),
          new DatosDePrueba("Comedor Los Pibes (corregido)", null, null, null, null));
    });
    EntidadBeneficiaria actualizada = repository.findById(entidad.getIdDominio());
    assertThat(actualizada.getRazonSocial()).isEqualTo("Comedor Los Pibes (corregido)");
  }

  @Test
  void unPatchVacioNoModificaNadaDeLaEntidad() {
    EntidadBeneficiaria entidad = unaEntidadDePrueba();
    withTransaction(() -> {
      repository.registrar(entidad);
      repository.actualizarInstancia(entidad.getIdDominio(),
          new DatosDePrueba(null, null, null, null, null));
    });

    EntidadBeneficiaria actualizada = repository.findById(entidad.getIdDominio());
    assertThat(actualizada.getRazonSocial()).isEqualTo("Comedor Los Pibes");
    assertTrue(actualizada.getDireccion().esLaMismaQue(direccionCualquiera()));
    assertThat(actualizada.getNumeroBase()).isEqualTo("1145678900");
    assertThat(actualizada.getMails()).containsExactly("comedor@lospibes.org");
  }

  @Test
  void siElIdNoExisteActualizarInstanciaLanzaExcepcion() {
    assertThatThrownBy(() -> repository.actualizarInstancia("id-inexistente",
        new DatosDePrueba(null, null, null, "111", null)))
        .isInstanceOf(RecursoNoEncontradoException.class);
  }
}
