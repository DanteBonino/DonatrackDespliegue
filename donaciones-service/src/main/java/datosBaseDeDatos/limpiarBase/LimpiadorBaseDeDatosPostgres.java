package datosBaseDeDatos.limpiarBase;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class LimpiadorBaseDeDatosPostgres implements WithSimplePersistenceUnit {

  public void limpiar() {
    withTransaction(() -> {
      entityManager().clear();
      entityManager().createNativeQuery("""
        DO $$ DECLARE
            r RECORD;
        BEGIN
            -- Se cambia 'public' por el esquema definido en tu persistence.xml
            FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'donaciones') LOOP
                EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
            END LOOP;
        END $$;
        """).executeUpdate();
    });
  }
}