package datosBaseDeDatos;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class LimpiadorBaseDeDatosPostgres implements WithSimplePersistenceUnit {

  public void limpiar() {
    withTransaction(() -> {
      entityManager().clear();
      entityManager().createNativeQuery("""
        DO $$ DECLARE
            r RECORD;
        BEGIN
            FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'logistica') LOOP
                EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
            END LOOP;
        END $$;
        """).executeUpdate();
    });
  }
}