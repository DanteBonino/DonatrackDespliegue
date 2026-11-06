# EndpointGroup de apiBuilder

## ¿Qué hace?
- Una interfaz funcional (SAM) con un solo método: `addEndpoints()`
- `apiBuilder(...)` recibe ese EndpointGroup y, antes de ejecutar addEndpoints(), deja guardada una referencia (una variable estática, scopeada a esa llamada) apuntando a "cuál es el router activo ahora mismo". Recién ahí llama a addEndpoints(), y cuando termina, borra esa referencia

## ¿Por qué usarlo?
- Porque before(...), get(...), path(...), etc. dentro del AppRouter no son métodos de la clase ni de config.routes — son métodos estáticos de io.javalin.apibuilder.ApiBuilder
- Esos métodos estáticos, al ejecutarse, van y leen esa variable que apiBuilder() dejó seteada momentos antes, para saber a qué router/config engancharse.
- Es decir: EndpointGroup es la "caja" que le permite a apiBuilder() controlar exactamente cuándo y con qué contexto corre el código, para que esos métodos estáticos sepan a dónde registrarse sin que haya que pasarles el config explícitamente en cada línea.
