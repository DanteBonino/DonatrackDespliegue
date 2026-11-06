package Repositories.Beneficiarios;

import Controllers.EntidadesBenefeciarias.DatosEntidadesBeneficiarias;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ExceptionHandler.EntidadYaExisteException;
import Repositories.Generales.RepositorioConSoftDelete;
import Repositories.Generales.RepositorioDeActualizable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeneficiariosRepository implements IBeneficiariosRepository, RepositorioConSoftDelete<EntidadBeneficiaria>, RepositorioDeActualizable<EntidadBeneficiaria, DatosEntidadesBeneficiarias> {
  private static BeneficiariosRepository INSTANCE = new BeneficiariosRepository();

  public static BeneficiariosRepository getInstance() {
    return INSTANCE;
  }

  private BeneficiariosRepository() {

  }

  @Override
  public List<EntidadBeneficiaria> getInstancias() {
    return RepositorioConSoftDelete.super.getInstancias();
  }

  @Override
  public EntidadBeneficiaria findById(String id) {
    return RepositorioConSoftDelete.super.findById(id);
  }

  @Override
  public void registrar(EntidadBeneficiaria nuevaEntidad) {
    RepositorioConSoftDelete.super.registrar(nuevaEntidad);
  }

  @Override
  public void actualizarInstancia(String id, DatosEntidadesBeneficiarias nuevosDatos) {
    RepositorioDeActualizable.super.actualizarInstancia(id, nuevosDatos);
  }

  @Override
  public void eliminarInstancia(String id) {
    RepositorioConSoftDelete.super.eliminarInstancia(id);
  }

  public Class<EntidadBeneficiaria> clase() {
    return EntidadBeneficiaria.class;
  }

  public void validarActualizacion(EntidadBeneficiaria unaEntidad, DatosEntidadesBeneficiarias nuevosDatos) {
    //No se valida nada
  }

  @Override
  public void validarUnicidadDeNegocio(EntidadBeneficiaria nuevaEntidad) {
    if (existeAlgunaEntidadBeneficiariaConEstaRazonSocial(nuevaEntidad.getRazonSocial())) {
      throw new EntidadYaExisteException(
          "Ya existe una entidad beneficiaria cuya razon social es " + nuevaEntidad.getRazonSocial());
    }
  }

  private boolean existeAlgunaEntidadBeneficiariaConEstaRazonSocial(String razonSocial) {
    return existeSegun("razonSocial", razonSocial);
  }

  public EntidadBeneficiaria buscarPorEmail(String email) {
    String jpql = "SELECT e FROM EntidadBeneficiaria e " +
        "JOIN e.datosDeContacto c " +
        "JOIN c.mails m " +
        "WHERE m = :email";

    return entityManager()
        .createQuery(jpql, EntidadBeneficiaria.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public EntidadBeneficiaria buscarPorEmailConNecesidades(String email) {
    String jpql = "SELECT DISTINCT e FROM EntidadBeneficiaria e " +
        "JOIN e.datosDeContacto c " +
        "JOIN c.mails m " +
        "LEFT JOIN FETCH e.necesidades n " +
        "LEFT JOIN FETCH n.condicionDeAceptacion ca " +
        "LEFT JOIN FETCH ca.subCategoria " +
        "WHERE m = :email";

    return entityManager()
        .createQuery(jpql, EntidadBeneficiaria.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public List<EntidadBeneficiaria> listarConDatosDeContacto() {
    String jpql = "SELECT DISTINCT e FROM EntidadBeneficiaria e " +
        "LEFT JOIN FETCH e.datosDeContacto " +
        "WHERE e.estaEliminado = false";

    return entityManager()
        .createQuery(jpql, EntidadBeneficiaria.class)
        .getResultList();
  }

  public Map<Long, Long> contarNecesidadesPorEntidad() {
    String jpql = "SELECT e.id, COUNT(n) " +
        "FROM EntidadBeneficiaria e " +
        "JOIN e.necesidades n " +
        "WHERE e.estaEliminado = false AND n.estaEliminado = false " +
        "GROUP BY e.id";

    List<Object[]> resultados = entityManager()
        .createQuery(jpql, Object[].class)
        .getResultList();

    Map<Long, Long> conteos = new HashMap<>();
    for (Object[] fila : resultados) {
      Long entidadId = (Long) fila[0];
      Long cantidad = (Long) fila[1];
      conteos.put(entidadId, cantidad);
    }

    return conteos;
  }

  public long contarBeneficiariasActivas() {
    String jpql = "SELECT COUNT(e) FROM EntidadBeneficiaria e WHERE e.estaEliminado = false";
    return  entityManager()
        .createQuery(jpql, Long.class)
        .getSingleResult();
  }
}
