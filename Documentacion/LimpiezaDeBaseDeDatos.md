# Limpieza de Base de Datos (`LimpiezaDeBaseDeDatos`)

Interfaz (Mixin) para garantizar un estado limpio en la base de datos antes de cada test de integración, evitando la contaminación entre pruebas.

- **Específica de HSQLDB:** Utiliza `SET DATABASE REFERENTIAL INTEGRITY FALSE`, un comando nativo y exclusivo de este motor relacional. Si se migra de BD, este script debe adaptarse al nuevo dialecto.
- **Vaciado de Caché (L1 Cache):** Se ejecuta `entityManager().clear()` al iniciar para evitar que Hibernate intente sincronizar (vía *flush*) el estado residual de la memoria con la base de datos, lo cual provocaría errores al no encontrar los registros físicos.
- **Bypass de Foreign Keys:** Se deshabilita temporalmente la integridad referencial para poder iterar y borrar todas las tablas dinámicamente sin que la base de datos rechace la operación por dependencias entre entidades (orden de borrado).
- **Restauración de restricciones:** Se reestablece la integridad referencial al finalizar para garantizar que los tests corran bajo las reglas reales de la base de datos.