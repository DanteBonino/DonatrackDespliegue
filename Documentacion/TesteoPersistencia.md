# Testeo Persistencia

## Tests donde se levanta un Servidor
- Agregar el Filtro global
- Agregar el ExceptionHandler
- Si hay un setUp donde se deben persistir cosas es importante agregar el withTransaction en el BeforeEach porque esa parte no esta cubierta por lo que te provee la interfaz
- Para iniciar con la BDD vacía antes de cada test: Agregar la interfaz **LimpiezaDeBaseDeDatos**
