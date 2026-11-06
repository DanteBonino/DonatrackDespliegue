package Repositories.Donantes;

import DTOs.DatosDonante;
import Donantes.Donante;
import Repositories.Generales.RepositorioConSoftDelete;
import Repositories.Generales.RepositorioDeActualizable;
import Repositories.Generales.filtros.Filtro;
import Usuario.ConSesion;
import java.time.LocalDate;
import java.util.List;

public class DonantesRepository implements IDonantesRepository, RepositorioConSoftDelete<Donante>, RepositorioDeActualizable<Donante, DatosDonante> {
  private static DonantesRepository INSTACE = new DonantesRepository();

  public static DonantesRepository getInstance() {
    return INSTACE;
  }

  private DonantesRepository() {

  }

  @Override
  public List<Donante> getInstancias() {
    return RepositorioConSoftDelete.super.getInstancias();
  }

  @Override
  public Donante findById(String id) {
    return RepositorioConSoftDelete.super.findById(id);
  }

  @Override
  public void registrar(Donante nuevaEntidad) {
    RepositorioConSoftDelete.super.registrar(nuevaEntidad);
  }

  @Override
  public void actualizarInstancia(String id, DatosDonante nuevosDatos) {
    RepositorioDeActualizable.super.actualizarInstancia(id, nuevosDatos);
  }

  @Override
  public void eliminarInstancia(String id) {
    RepositorioConSoftDelete.super.eliminarInstancia(id);
  }

  public Class<Donante> clase() {
    return Donante.class;
  }

  public void validarActualizacion(Donante unaEntidad, DatosDonante nuevosDatos) {
    IDonantesRepository.super.validarActualizacion(unaEntidad, nuevosDatos);
  }

  public Donante buscarPorEmail(String email) {
    String jpql = "SELECT d FROM Donante d " +
        "JOIN d.datosDeContacto c " +
        "JOIN c.mails m " +
        "WHERE m = :email AND d.estaEliminado = false";

    return entityManager()
        .createQuery(jpql, Donante.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public boolean elDocumentoYaEstaEnUso(String documento) {
    return existeSegun("documento", documento);
  }

  public boolean elCuitYaEstaEnUso(String cuit) {
    return existeSegun("cuit", cuit);
  }

  public long contarDonantesActivos() {
    String jpql = "SELECT COUNT(d) FROM Donante d WHERE d.estaEliminado = false";
    return entityManager()
        .createQuery(jpql, Long.class)
        .getSingleResult();
  }
}
