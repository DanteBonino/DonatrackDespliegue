package utils;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.BeforeEach;

public interface ConLimpiezaDeBaseDeDatos extends WithSimplePersistenceUnit {

  @BeforeEach
  default void limpiarBaseDeDatos() {
    withTransaction(() -> {
      entityManager().clear();
      entityManager().createNativeQuery("SET DATABASE REFERENTIAL INTEGRITY FALSE").executeUpdate();
      entityManager().getMetamodel().getEntities().forEach(entidad -> {
        createQuery("DELETE FROM " + entidad.getName()).executeUpdate();
      });
      entityManager().createNativeQuery("SET DATABASE REFERENTIAL INTEGRITY TRUE").executeUpdate();
      return null;
    });
  }
}