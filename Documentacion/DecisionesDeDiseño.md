# Decisiones de Diseño

## Estado de las donaciones
- Podrían ser enums, pero justo ENTREGAFALLIDA tiene estado interno y, por ende,
  los enums quedan cortos ya que no tiene estado interno mutable (se define en compilación).
- Podrían definirse subclases por cada Estado así cada uno responde a un mensaje.
  Esta implementación complejiza la representación de los estados y agrega complejidad
  accidental debido a la cantidad de entidades necesarias. Pero cumple con el requeri-
  miento de que haya estados con estado interno mutable.
- Todavía no está claro si los mensajes son necesarios -> eliminados
- Por ahora gastar la herencia de los estados sólo para definir qué mensajes deben ex
  poner es innecesario, dado que es una bala de plata. Entonces, con definir una inter
  faz compartida alcanza y sobra, ya que cumple el mismo objetivo y no desperdicia la
  herencia.
- Cada estado sabe a qué estado pasar, esto genera un acoplamiento mayor con Donacion,
  ya que cada Estado va a setearle un Estado a la misma; pero ahorra validaciones
  asegura consistencia y le quita la responsabilidad a la Donacion de saber a qué Esta
  pasar.
- La alternativa anterior se cae en cuanto aparecen los estados que permiten transicionar a
  más de 1 estado.
- Una alternativa es que cambiarDeEstado reciba el nuevo estado y la donación delegue en su
  estado la validación del cambio. Es fácilmente extensible, pero tiene el codeSmell de type
  check.
- Otra alternativa es que todos conozcan los mensajes de transición, donde se llame al setter que tiene la validacion,
  la validacion te da false por default y cada Estado pisa con true los que sabe que puede.
- La última alternativa es hacer una especie de double-dispatch donde lo que se hace es validar
  en el estado que el estado elegido es válido, pero en lugar de hacer un typecheck el otro estado sabe
  decir si es transicionable o no. Ventaja -> no type check | Desventaja -> la interfaz de los estado crece
  lo cual es menos extensible y mantenible, la clave está en si los estados van a variar o son estos nomas.
- Una clave es la justificación que sólo pertenece a las EntregasFallidas. Es un problema análogo
  al aceptar/rechazar en lista de correos. Podríamos forzar un polimorfismo entre los distintos
  estados, pero esto implica agregar en la intefaz donacionIndependiente la lógica justificación
  cuando no tiene sentido porque eso sólo caracteriza a EntregaFallida y no al resto.
- Tener subtipos de Donaciones independientes no tiene sentido porque necesitas transicionar
  a otros estados y tener trasabilidad.
- Una opción es que todas sean clases y la otra es que las que sean statless sean enums y Entrega
  Fallida sea únicamente una clase.
- La mayoría de estados podrían ser Singletons, o podrías tener en la clase EstadoDonacion métodos
  statics para cada estado que no queres que varie -> das lugar a que se creen estados raros.
- Actualmente se forzó un poco el abstract de la clase EstadoDonacion para así tener bien claros
  los estados posibles. En mi opinión con el double dispatch queda más clean porque los estados
  quedan más simples, aunque más complicados de extender.

## Importador de CSV
- La parametrización permite pensar en trabajar con un Singleton o, al menos, de
  de trabajar con una misma instancia de Importador sin tener que modificar nada,
  favoreciendo la inmutabilidad.
- El buffer no se inyecta porque es necesario saber la ruta que vas a tener que
  usar para inicilizarlo.
- Por una cuestión de SIMPLICIDAD se saltea el encabezado y se asume que los csvs
  respetan el formato del ejemplo y el orden en los parámetros. Se podría hacer
  más genérico y leer el encabezado para reconocer el orden de los campos, pero
  como no sabemos si pueden venir en otro orden, simplemente lo dejamos así.
- La creación de las instancias podría estar dentro del importador, lo cual sería
  menos cohesivo que si la instanciación estuviera en otro lugar.
- Hoy por hoy no tiene mucho sentido complejizar el sistema en pos de ganar flexibi
  lidad, entonces el creador de instancias va a estar acoplado al formato de las lí-
  neas que trae el importador. Pero en un futuro se podría wrapear a los importa-
  dores para que todos devuelvan con un mismo formato que sea el que espera el
  creador de instancias.
- También se podría abstraer la idea de CreadorDeInstancias y buscar la forma en
  que se parametrice qué tipo de instancias crear o algo así, pero no es
  necesario por el momento.
- Si bien hoy tener un "adaptador" entre las líneas del que importa y el que crea no
  es necesario, ya que no le das otro uso a lo que importas. Pero tener un intermedia-
  rio no agrega una gran complejidad y te da la ventaja de que el creador de instan-
  cias no tenga  que fijarse cómo vienen los datos para después trabajar con ellos.
- Al final se cambió la implementación para usar una biblioteca, lo cual simplifico el diseño.
  La decisión se basó en seguir una recomendación que había en el foro para el TPA de 2024 o
    2025.

## Necesidades
- Para simplificar la idea es que lxs usuaries puedan cargar los bienes que necesitan
  y el sistema por detras va a generar "x" necesidades según la cantidad de bienes de
  distinto tipo que se carguen (entiendase tipo como subtipo + valor de agrupamiento).
- Podría ser una clase abstracta que agrupe la lógica de ser una Necesidad y deje sin definir
  los métodos que sus subclases deben tener para poder ser instanciables. No se busca aprovechar
  una estructura de alrogitmo común entre subclases y que ellas definan partes particulares,
  sino que se intenta denotar claramente la idea de qué es una Necesidad y la jerarquía existente.
- A raíz de lo último dicho, tranquilamente podría no existir una clase abstracta y que Necesidad
  Recurrente herede de NecesidadExtraordinaria (no al revés porque la primera agrega la lógica del
  periodo), pero, si bien es más Simple (requerís menos entidades), la jerarquía de Necesidades pasa
  a ser menos clara y estás forzando una relación que, en este punto del trabajo, no está tan clara.
- Se decidió no validar cuestiones como si el Bien recibido es del tipo precisado o si tiene sentido
  recibir donaciones dado que se asume que la clase ya es una "zona" segura de nuestro sistema.

## Necesidades Extraordinarias
- Son Necesidades que no tienen caducidad, únicamente dejan de estar activas cuando fueron satisfechas.

## Necesidades Recurrentes
- Un primer enfoque fue que se deban "recargar" cada N tiempo (siendo N el periodo de validez elegido
  para cada necesidad recurrente). Pero esto se descartó por no ser coherente con la idea de Recurrente.
  Es decir, estaría bueno que si es Recurrente se pueda asentar 1 vez y listo.
- El segundo enfoque buscó la "automatización" de la recurrencia. Lo que obligó a que los Bienes recibidos
  tuvieran que tener una fecha para diferenciar los bienes históricos de los actuales.
  El hecho de tener que agregar una fecha a los Bienes hace que quizá la idea de DonacionIndependiente cobre
  más fuerza y que los Bienes no tengan la lógica del Estado.
- Si bien no es deseable setear y gettear en la misma operación, simplemente por motivos de performance
  se cachea el último periodo calculado para no arrancar siempre desde 0.
- El método con setter y getter se decidió que sea el que te da la fecha del periodo actual, porque
  a él lo usan métodos y él no usa métodos. Entonces, de esta forma te garantizas consistencia.

## Periodo
- Nuevamente surgen enfoques que son en cierto grado bastante parecidos en cuanto a simplicidad.
  Un primer enfoque es modelarlo como un INT/INTEGER que represente la cantidad de días. Lo cual
  es sumamente simple y flexible en el sentido de que no tenes predefinidos periodos estáticamente.
  Un segundo enfoque sería trabajar con Enums que puedan decir cuántos días significan. Este enfoque
  es menos flexible, ya que normaliza los datos y la cantidad existente. Pero te deja abierta la puerta
  a agregar comportamiento según el Periodo. Es una buena pregunta cuánta libertad queremos tener a la hora
  de definir los periodos.
- Finalmente descubrimos que en java ya existe una idea que lo soluciona "Period". Por ende,
  decidimos utilizarla.
- Por ahora como no hay restricciones sobre cuándo deben empezar los periodos (ej: días hábiles),
  simplemente se asume que el periodo arranca el día de creación.

## Bien vs DonacionIndependiente
- Hay dos formas de pensar las donaciones Independientes:
- Pensarlas como que son lo mismo que un Bien, esto hace que
  los bienes tengan incorporados el EstadoDonación. Esta idea
  hace que el diseño sea más simple que la otra forma porque
  necesitas menos entidades. La clave está en cómo vas a trabajar
  con los Bienes una vez ingresan a tu Sistema. Por ahora, parece que
  desde que ingresan ya están atados a la idea de la donación y su estado.
  Con lo cual no necesitas ni te beneficias de separar la idea de Bien del
  estado. Lo único es que hace que el Bien pase a ser Mutable, ya que puede
  y debe variar su estado.
- Tener la idea de Donación Independiente que conozca un Bien y tenga un Estado.
  Hace que la interfaz de los Bienes esté más limpia, ya que no van a contar con
  las operaciones de los Estados. Además, garantiza que el Bien sea inmutable.
  Y posibilita que si, en algún momento, surge una entidad que comparte esta lógica
  de los Estados con Bien, sea fácilmente agregable (Extensibilidad). Lo malo es que
  agrega cierta Complejidad (mínima).
- La realidad es que por ahora cualquiera de las dos es totalmente válida. Por eso
  vamos por la más simple y, si más adelante, surgen requerimientos que
  justifiquen más la otra alternativa el cambio no es tan difícil de hacer.
- A raíz de que en Necesidades se necesita agregar una fecha, se decidió por separar la idea
  de Bien de Donación Independiente.

## Donación Independiente
- Aparece a partir de tener que agregar Estado y fecha de donación a los Bienes.
- Si bien la inyección de dependencias a lo largo de la cursada se presentó como algo deseable,
  por sobre Entidades que instancien dependencias. En este caso las donaciones cuando se crean
  siempre se van a crear con la Fecha actual y con Estado Deposito. Es cierto que inyectándole
  esas dependencias se gana en cuestiones como testeabilidad, pero también abris la puerta a que
  se creen Bienes con condiciones iniciales indeseadas. Si bien podrías crearlos a partir de un
  componente externo que sepa que siempre que cuando creas una Donacion la queres con esas condiciones,
  esto agregaría complejidad extra para obtener beneficios que en este momento no parecen ser tan
  necesarios.
- Se usa !isBefore y no isAfter porque no toma las del mismo día.
- Se decidió parametrizar el Estado de la DonaciónIndpendiente simplemente por una cuestión
  de testeabilidad. En cambio, la fecha se crea siempre como la actual y no se precisa crear
  donaciones con otra fecha ni para testearlas, entonces no se la parametrizo.

## Donante
- Se hace a Donante con herencia dado que tanto Persona Humana como Persona Juridica
  tienen el mismo rol hacia el sistema (hacer donaciones) pero los mismos poseen distintos atributos.
  En esta iteracion solamente comparten pocos metodos, en el caso de que realicen lo mismo de manera diferente
  la clase de herencia correspondiente deberá reescribir el metodo.
- En Persona Juridica se entiende a los representantes como una lista de un objeto aparte,
  dado que estos podrían en futuras iteraciones realizar más acciones en relacion a la representacion
  de la Persona Juridica. De momento estos solo poseen un medio de contacto para distinguirlos.
  Por lo que otra opción para instaciarlos sería solamente dicho medio como atributo de la persona juridica y no como clase,
  por ahora se toma la primera opcion como para abstraerlo como un objeto distinto.
- Se decidió que tenga la responsabilidad de hacer el refrescar para no forzar polimorfismos absurdos entre Subtipos de Donantes
- Por ahora se asume que los archivos de Csv van a venir completos, si no el refresh sería un
  poco más complicado porque habría que chequear que tenga sentido el valor nuevo.

## Deposito
- La clase Deposito se entiende como el objeto que más interactúa con el sistema teniendo el rol de registrar los usuarios y las donaciones en el mismo.

## DonantesRepository
- Hoy por hoy se encarga de registrar donantes y de actualizar las instancias existentes según el archivo de csv que se le pase.
  Podría ser más cohesivo si sólo se encargase de cuestiones de add + fetch, pero si sacas la responsabilidad de actualizar a quién se la asignas?
  Y cómo haces para que no quede como un Facade?

## DonanteDto
- Constructor vacío o constructor con todos los parámetros.
- El constructor vacío era necesario sí o sí por que así lo pide la biblioteca de csvs
- El constructor completo es demasiado largo para que sea cómodo
- Se podría tener 3 constructores lo cual si bien no es recomendable, me permite
  no tener que definir setters innecesarios ni complejizar el diseño con un builder

## MedioDeComunicacion
- Permite que no se comunique con el usuario únicamente por el medio de preferencia
- El medio de preferencia/conocer recibirNotificacionPreferente hace que no haya que hacer un if en
  ningún lado ni tenes que mapear un enum para convertilo al medio de preferencia adecuado.
- Tiene la lógica de qué pasa cuando falla la notificación:
    - Creo una notificación pendiente que eventualmente se va a intentar reenviar
    - Podría ser una lista que conozcan los notificables, pero esto hace que sea más dificil de
      intentar reenviarlo porque tendría que tener un Repository de Notificables o debería saber qui
      nes son todos los notificables de mi sistema
    - Podría tener un Repository de Notificaciones que se intentaron enviar al menos una vez, no
      interesa registrar notificaciones que se crearon pero nunca se intentaron enviar, al menos
      por ahora.

## Repository de Notificaciones
- Hoy por hoy se resuelve el agregado de elementos con agregar si no existe y actualizar en caso
  contrario. Pero a futuro habría que ver si hay que considerar la concurrencia.

## Notificaciones - Estado
- Originalmente planteamos Notificacion con 2 Estados polimórficos (Pendiente / Entregada), pero
  para los requerimientos actuales alcanza con un booleano (fueEntregada) y un contador de rebotes
  (vecesRebotada), sin necesidad de la complejidad de una jerarquía de Estados.
- Decidimos no aplicar State todavía porque ambos "estados" no tienen comportamiento propio más
  allá de habilitar o no el envío (esEnviable). Aplicar el patrón ahora sería generalizar sin un
  segundo caso real que lo justifique (YAGNI).
- Si en el futuro aparece un estado con comportamiento propio (ej. reintentos con backoff,
  notificación cancelada, etc.), ahí sí pasaríamos a Estados polimórficos.

## Notificable/Destinatario
- Se plantearon 2 alternativas:
- Una única interfaz Destinatario/Notificable
- Múltiples interfaces para los distintos medios y una clase que implementé todas las interfaces
- La primer alternativa es más simple ya que requiere menos entidades, pero es menos cohesiva y
  flexible dado que obliga a siempre, para se notificable, tener que se notificable por todos los
  medios.
- La segunda permite que existan Entidades notificables por un único medio como Entidades notifica-
  bles por N medios distintos.
- Además, agrega la lógica de MedioPreferencial como algo más complejo que un Enum que, por un lado,
  complejiza, en cierta medida, la construcción de Notificable/Destinatario; pero, por otro lado,
  simplifica la forma en que se recibe una notificación por el medio de preferencia, ya que es sim-
  plemente delegarlo en aquello que es Notificable que conoce su medio Preferencial.
- En la otra versión aquello Notificable debía conocer su medio preferencial, un Enum, pero, como
  era un Enum, en otro lugar había que tener un Diccionario o un switch para conseguir el medio co-
  rrespondiente.
- La versión de los Enums hace que aquellas entidades del dominio que se pretenden persistir sean
  más sencillas de persistir; sin embargo, agrega complejidad en otras entidades que NO son persis-
  tibles (!)

## NotificableMail
- Como los donantes tienen 1 sólo mail y las entidades beneficiarias tienen N mails, entonces
  hay un problema con respecto al medio de comunicación mail, ya que: ¿qué espera recibir?¿ 1 o va-
  rios mails?
- Si se los trataba distinto, entonces ya los MediosDeNotificacion no se podían tratar polimórfica-
  mente.
- Por ende, se planteo la idea de mailsParaNotificar de forma que SIEMPRE devuelva una lista de
  mails. Logrando así no perder el polimorfismo.
- En un futuro MedioCorreo podría requerir que lo que reciba no sea un Notificable, sino un
  Notificable Mail.

## Notificable vs Usuarix
- ¿Son dos cosas distintas?¿Existen Usuarios no notificables?
- Como todavía son interrogantes de los que no se tiene tanta información se decidió no plantear una
  relación directa entre los Notificables y los que son Usuarixs. Realmente no parece ser un cambio
  que, en el futuro, sea difícil de hacer.
- Por ahora, Donante tiene una idea de sesión, pero Entidad Beneficiaria no. Por esto es que se de-
  cidió patear la pelota para adelante y no generar una relación entre ambas ideas.

## Notificacion de Bienvenida
- Hoy por hoy no hay nada que haga preveer que se vaya a requerir agregar nuevas acciones una vez
  se registra una Donante ni tampoco que se vaya a necesitar configurar estas acciones en runtime.
- Siempre que se registra a un Donante se va Enviar el mail de bienvenida.
- Plantearlo con un Observer tiene como ventaja que permite testear fácilmente porque, en los casos
  donde no se requiera enviar un mail realmente, no se agrega ningún observer o, simplemente, se
  mockea el observer y ya. Pero, realmente nos da flexibilidad que hoy no necesitamos, aumenta la
  la complejidad en cantidad de entidades/entendimiento y genera la necesidad de validar que esté
  el observer del mail o de complejizar la configuración del mismo.
- Plantearlo de una forma INTERACTIVA complejiza la construcción del Repository porque se va a tener
  que inyectar la dependencia del MedioCorreo y disminuye la cohesión del método registrar porque
  no sólo se agrega al Donante, sino que también debe saber que tiene que enviar la notificación y
  qué notificación. Sin embargo, no te da lugar a que tengas que preocuparte por si está o no el
  Observer de EnviarMailDeBienvenida.
- Tener en el repository más lógica que la de proveer instancias y guardarlas es poco cohesivo, por
  ende falta una abstracción

## Testeo de MedioDeNotificacion
- Se uso un testDouble y no mock porque el método a Stubbear tenía restricción protected.

## Cómo obtener lo que necesitas (mail/s, teléfono) en cada MedioDeNotificacion
- Una opción es hacer notificacion.getReceptor.get..., ya que receptor es Notificable y, por ende,
  va a poder responder a lo que necesite. Pero esta opción rompe el encapsulamiento y genera un, de forma que
  fuerte acoplamiento entre la implementación de Notificacion y los Medios de Notificaciones.
- Otra forma sería que Notificación sepa responder a getMailsParaNotificar y getTelefono, pero esto
  claramente no escala si surgen nuevos medios de comunicación. La interfaz se volvería gigante y
  estaría todo el tiempo actuando de MIDDLE MAN.
- Otra forma es mediante herencia, es decir planteando una jerarquía de Notificaciones que sepan
  responder a medioDeComunicacion o informacionBásicaDeComunicacion y que cada una responda lo que
  verdaderamente necesita.
- Una reversión de lo anterior sería con composición, lo cual tiene más sentido si se pretende
  poder enviar la Notificacion por distintos medios.
- Las dos últimas opciones quizá te atan a saber a qué medio la

## Testeo de ClienteCorreoSendGrid
- Hay lugares donde hubo que usar excepciones chequeadas porque así está definida la api, en los
  lugares donde llamamos al método nuestro que wrappea la llamada a la api en un try/catch no hizo
  falta
- Mockito puede mockearla porque la clase SendGrid en ningún lugar usa la palabra final (!):
- porque internamente genera una subclase dinámica en tiempo de ejecución para interceptar las
  llamadas — y no se puede extender una clase final en Java.

## Notificaciones Fijas vs Preferenciales
- Si las notificaciones se deben enviar ante un intento fallido

## Flujo de enviar una Notificacion
- Si las notificaciones saben enviarse y hay una categoría preferencial vs fija:
- Crear la notificacion
- Pedirle que se envie
- Esto no es tan terrible cuando lo planteas con Observers porque es inevitable que haya cierto aco-
  plamiento entre el Observer y la forma en que se envia la notificacion.
- El problema principal es que tenes 3 formas de enviar Notificaciones y, en realidad, siempre que-
  res usar una, pero no tenes forma de forzar a que esto sea así siempre. Sólo lo podes documentar y
  confiar en que se va a usar esa lógica.
- Además, que no tenes forma de hacer respetar que las fijas siempre se manden por el mismo medio.
- Podrías hacer validaciones, pero implican complejidad (Double dispatch o saber demasiado sobre
  los medios) o un typetest.
- Es más difícil reconstruir la instancia cuando se la trae de la Base de Datos.
- Sí tenes un sistema de Notificaciones:
- Siempre le vas a pedir al sistema de notificaciones porque las notificaciones dejan de saber en-
  viarse y los notificables dejan de saber recibirlas.
- Acá debería seguir habiendo una lógica de Notificación Fija vs Preferencial.
- No debes validar nada.
- No pueden convivir distintos medios del mismo tipo o tenes que tener tantos enums como medios
  medios del mismo tipo haya (deja de existir el enum MAIL y pasa a existir SENDGRID).
- Si tenes subtipos de Notificaciones según el Medio de Notificación:
- Agrega complejidad al nivel de las Notificaciones, ahora tenes N tipos de Notificaciones.
- Se duplica la jerarquía de Medio en Notificacion

## Notificaciones Fijas vs Preferenciales
- Suponiendo que puede haber varias formas de mandar mail al mismo tiempo en el sistema:
- Si las notificaciones tienen el medio (la instancia real), lo que pasa es que si hubiera una
  instancia del mismo tipo de medio y quisieras enviar la notificación por ese medio, entonces no podes
  validar si es posible preguntando por la instancia, tenes que hacer que todas los medios del mismo tipo
  sepan si otra instancia es del mismo tipo de medio agregando un type test.

## ¿Se podría desacoplar MedioDeNotificacion y Notificacion?
- Actualmente hay un acoplamiento porque el "enviarNotificacion" de MedioDeNotificacion tiene lógica
  de "persistir" la notificación + try/catch con cambios de estado en la Notificación.
- Se podría sacar eso de MedioDeNotificación, pero también das pie a enviar mensajes y que no quede
  registrado.

## Jerarquía Usuario
- En principio, por Yagni, podríamos plantear toda la lógica de Usuario en Donante y listo. Nada te
  dice que vaya a existir razón alguna para generalizar la idea de Usuario.
- Sin embargo, disminuye la cohesión de Donante tener esa lógica ahí, dado que parece estar más rela
  cionado a la idea de sesiones. Entonces, Donante pasaría a tener lógica de Donante + Sesión.
- Podría ser una interfaz, pero se requiere estado interno específico y comportamiento, por ende una
  clase es necesaria.
- La clase se la definió como abstracta porque, hoy por hoy,no existe razón alguna para poder instan
  ciar Usuario.
- El problema de que Usuario sea una clase es que Notificable también lo es y Donante ya hereda de
  Notificable. Por ende, se planteó que Usuario herede de Notificable porque, en principio, no es muy
  descabellado pensar que TODOS los Usuarios serán Notificables y, además, para no forzar a que otras
  entidades Notificables del Sistema sean Usuarios.
- Sinceramente es una decisión que es apresurada y no hay suficiente información como para estar se-
  guros de que sea la correcta, pero, a su vez, es fácil de modificar y, por el momento, alcanza.

## Eventos y Notificaciones
- Hay 3 formas posibles:
- Tener un BusDeEventos + Eventos reificados de forma que TODOS los eventos del sistema estén en
  un sólo lugar:
- Tenes un diccionario o varios listas distintas o todos los observers saben responder a todos
  los eventos o hay una única forma de responder.
- Esta forma desacopla las entidades puras del dominio de la noción de suscriptores.
- Permite, únicamente, setear las mismas acciones para todas las intancias. No permite fácilmen-
  te que la instancia A de la clase B ante un evento desencadene ciertas acciones, mientras que la
  instancia C de la clase B desencadene otras.
- Tener en las instancias del dominio que se requiera la nocción de lista de observadores y la po-
  sibilidad de suscribir y desuscribir a los mismos.
- Que las instancias del dominio que deban notificar el suceso de un evento conozcan un handler
  y que sea ese handler el que tenga la noción de observadores y la posibilidad de suscribir/desus-
  cribir.
- Las dos últimas opciones es como que reparten la responsabilidad de los eventos en N lugares,
  mientras que la primera tiene todo en un mismo lugar.

## Notificar Inactividad
- Sacar la lógica de PublicadorDeInactividad y la referencia al mismo de Donante/Usuario hace que se
  simplifica la creación de instancias de Donantes, sobre todo teniendo en cuenta la creación desde Dtos.
- Se podría solucionar si Donante se creara siempre con la misma lista o si hubiera un factory de
  PublicadorDeInactividad.

## Algoritmos De Asignacion
- Sólo se ejecutan en la tarea programada, por ende, en lugar de tener una entidad que ejecute un
  algoritmo en particular y que tenga la posibilidad de cambiarlo, se eligió reificar los algoritmos
  y que la tarea programada reciba al algoritmo a ejecutar como una dependencia.
- Se jerarquizó los algoritmos de forma que haya algoritmos simples y algortimos compuestos. Estos
  últimos simplemente tienen una colección de algoritmos y los ejecutan y luego trazan la intersección.
- La reificación de los algoritmos hace que para agregar un nuevo algoritmo simplemente se tenga que
  crear una instancia que implemente la interfaz AlgoritmoDeAsignación, con lo cual es fácilmente exten
  sible en comparación a, por ejemplo, tener una clase que tenga cada algoritmo como método.
- Haber creado AlgoritmoMixto que sea polimórfico con AlgoritmoSimple hace que la combinación de algo-
  ritmos sea muy fácil de hacer.
- Es importante que la entidad que está en la posición 0 es la más prioritaria (!)

## Solicitud De Asignación
- Se creó la solicitud de asignación de forma de generar el asincronismo al que hace referencia el
  enunciado. Esa solicitud se crea y su aceptación/rechazo no tiene que ser sincrónica.

## Confirmar Asignación
- Una alternativa era tener una clase Administrador, un repositorio de administradores y que la tarea
  programada eligiera un administrador al azar y le crease una solicitud de asignación.
- Esto tenía como positivo que asociaba la responsabilidad a una clase de dominio de forma clara.
- Lo malo es que si interesaba que cualquier administrador pudiera aceptar cualquier solicitud,
  te lo imposibilitaba.
- La otra alternativa era reificar la acción sin que este asociada a una entidad de dominio y, en
  un futuro asociarla a un Controller
- Esta opción lo que intenta plasmar es la idea de que a todos los admins se les muestren las soli-
  tudes en el front y que ellos puedan aceptarlas a través de un endpoint y que el controller llame
  a esta acción.

## Entidades Beneficiarias y Necesidades
- Se modificó Necesidades para que, en lugar de tener una lista de DonacionesIndependientes, tenga
  una de ReciboDeDonaciones, que es la DonacionIndependiente + fecha de Asignación a la Entidad.
- Esto permite tener trazabilidad y arregló las Necesidades Recurrentes.

## Persistible
- Hay una tendencia a que aparezca un ID para identificar aquellas entidades que deban ser persisti
bles porque se necesita una clave para persistir y para las request HTTP:
  - ¿Se usa autogenerados o datos reales?
    - Los datos reales podrían llegar a cambiar (error de tipeo).
    - Los datos reales a veces son sensibles y no es muy seguro ni respeta los derechos de las perso
    nas el hecho de que terminen en nuestras URLs
    - Si todos son autogenerados, entonces nuestro sistema es más CONSISTENTE.
- Hoy por hoy no hay implementado un ORM o una BDD que se encargue de esto, por ende surge la duda
de dónde, quién y cómo se crea ese ID.
  - Cómo es sencillo ya que JAVA provee la siguiente herramienta: `UUID.randomUUID().toString();`
  - Quién/Dónde:
    - Idealmente no es algo con lo que se deba contar desde que se crea la instancia porque se rela-
    ciona a la persistencia, lo cual apunta a setearlo recién cuando se hace un save. Sin embargo,
    setearlo en un momento dado implica tener un setter y nada te impide que se lo setee en otro mo-
    mento. Entonces, para no dar esa posibilidad lo único que se me ocurre es que sea en el momento
    de instanciación.
- Podría ser una interfaz que defina sólo `getId()`, pero eso se queda corto con respecto a que siem-
pre la instanciación es la misma, por eso me inclino a que sea una clase `Persistible`.
- Hay un problema con que sea una clase que es: ¿algo Notificable es o debe ser Persistible? o 
simplemente se dió el caso en el que TODOS los Notificables son Persistibles?
- Una forma de resolver esto es decir Algo Notificable es Persistible, esto no plantea un problema
en si porque por ahora todos los Notificables son Persistibles y te permite mantener la jerarquía
actual sin problema.
- El caso inverso sí que no es cierto porque una Doncion No es Persistible (!).
- Otra solución es que Notificable pase a ser una interfaz
- Vamos a ir por la más incoherente que es generar una jerarquía Persistible -> Notificable únicamen-
te porque posibilita no tocar modificar Notificable y Persistible no sabemos si está bien como idea.
- Otra opción es componer Notificable en aquello que quiera ser Notificable, pero complejiza la con
figuración de aquello Notificable.

## Servidor
- Crear el ManejadorDeExcepciones aumenta la complejidad pero hace más legible y entendible el código

## DonantesController
- Hoy por hoy el create registra y devuelve la nueva instancia. Sin embargo, es posible que en el fu-
turo esto haya que cambiarlo porque hoy el ID se crea cuando se hace new, pero cuando haya un ORM y
una BDD esto no va a ser así. Entonces, quizá haya que cambiar registrarDonante para que tenga efecto
y retorne.

## Validaciones en la creación de Donante
- No tiene sentido poder crear un Donante sin los medios para notificarlo ni los otros campos.
- Entonces, hay algunas alternativas:
  - Builder: 
    - te separa la configuración del uso y unifica el lugar donde se hacen las validaciones. 
    - aumenta la complejidad.
    - nada te quita que alguien construya por fuera del builder, pero la idea es fomentar su uso y
    crear una costumbre.
  - Validaciones en las instancias del dominio:
    - más simples.
    - te dispersa las valdiaciones.
    - mezcla uso con configuración.
    - te cubre siempre.

## Builders DonanteHumano y Juridico
- A pesar de que ambos tienen Contacto se decidió no abstraer un DonanteBuilder porque agrega mucha
mas complejidad que simplemente tener el campo repetido en ambos. Además, Contacto permite centralizar
las validaciones sin repetirlas.

## DonanteDTOResponse
- Es una forma de sólo mostrar aquello que queremos porque sino JSON agarra todos los getters públi-
cos de la jerarquía.
- Se decidió por un único Response y no 2 dado que:
  - Es más simple
  - Quien consume la api recibe un JSON y la seguridad de los tipos de JAVA no aplica y un campo con
  null no es un bug, como si lo puede ser en la configuración de una entidad de dominio, sino que es
  información sobre que el campo no aplica.

## Validaciones
- Se hizo un de forma que se pueda acceder de cualquier lugar ya que no es problemático que sea así
y permite que cualquiera pueda utilizar validaciones con excepciones propias del dominio.

## Excepciones de Dominio vs IllegalStateException
- Se decidió reemplazar las IllegalStateException genéricas de Java por excepciones propias del dominio de Logística (ej. CamionNoDisponibleException, EstadoRutaInvalidoException).
- Motivo: IllegalStateException es una excepción de infraestructura/lenguaje que no expresa la regla de negocio violada. Al crear excepciones semánticas, el modelo es más explícito y autoexplicativo.
- Ventaja a futuro: Esto facilita enormemente la capa de Controladores REST (MVC Web), ya que permite mapear cada excepción a un código HTTP específico (ej. devolver un 409 Conflict si un camión no está disponible, o un 400 Bad Request si la ruta no tiene entregas) sin tener que hacer parsing de strings de mensajes de error genéricos.

## Integración con Planificador Externo de Rutas
### Separación entre DTOs y Dominio Puro
- Alternativa 1 (elegida): Crear una capa de DTOs (`DonacionDto`, `CamionDto`, `PlanificacionResponse`, etc.) que actúan como "traductores" entre el dominio y el JSON.
- Ventaja: El dominio permanece puro, sin conocer detalles de serialización ni estructuras externas. Si mañana el proveedor cambia el formato del JSON, solo tocamos los DTOs y el Adapter, no las clases de dominio.
- Desventaja: Hay que escribir código de mapeo entre dominio y DTOs.
- Justificación: La capa de DTOs actúa como un Anti-Corruption Layer (patrón de Domain-Driven Design). Protege el núcleo del negocio de la volatilidad de las integraciones externas. El costo de escribir los DTOs es mínimo comparado con el beneficio de mantener el dominio limpio y testeable.
- Alternativa 2: Usar directamente las clases de dominio (`DonacionIndependiente`, `Camion`, `Ruta`) para serializar a JSON y enviar al sistema externo.
- Ventaja: Menos código, no hay que mapear entre objetos.
- Desventaja: Acoplamos fuertemente el dominio a la estructura del JSON externo. Si el proveedor cambia su API, tenemos que tocar clases de dominio que representan reglas de negocio, no detalles de integración. Además, el dominio tiene comportamientos (métodos de transición de estado, validaciones) que no tienen sentido en un JSON plano.

### Patrón Adapter para el Planificador
- Alternativa 1 (elegida): Implementar un `PlanificadorRutasAdapter` que encapsula toda la lógica de integración (serialización con Gson, división en lotes, envío HTTP simulado).
- Ventaja: El dominio solo conoce al Adapter a través de una interfaz o simplemente lo invoca como un componente de infraestructura. El Adapter es el único que sabe "que existe" un servidor externo al que hay que hacerle un POST.
- Desventaja: Una clase más.
- Justificación: El Adapter es el punto de contacto entre dos mundos con responsabilidades distintas: el dominio que dice "necesito planificar estas donaciones con estos camiones" y la infraestructura que dice "tengo que enviar un JSON a una URL". Separar estas responsabilidades hace que el código sea más cohesivo y fácil de testear. Además, si mañana cambiamos de proveedor de planificación, solo reemplazamos el Adapter, no tocamos el dominio.
- Alternativa 2: Que la lógica de dominio llame directamente a un cliente HTTP (ej. usar `HttpClient` que ya incorpora Java 11) para enviar el POST al planificador.
- Ventaja: Menos clases.
- Desventaja: El dominio queda acoplado a detalles de infraestructura (URLs, headers HTTP, manejo de conexiones). Viola el Principio de Inversión de Dependencias.

### Lógica de Lotes de 100 Donaciones
- El Adapter divide automáticamente la lista de donaciones en lotes de máximo 100 antes de enviar cada POST.
- Si hay 504 donaciones, se hacen 6 POSTs en lugar de 1.
- La restricción de "máximo 100 donaciones por ejecución" es un detalle de integración, no una regla de negocio. Por lo tanto, debe vivir en el Adapter, no en el dominio. El Adapter encapsula esta limitación y la aplica de forma automática, garantizando que la integración funcione correctamente sin importar cuántas donaciones haya.

### Manejo del Callback Asíncrono
- Alternativa 1 (elegida): El Adapter envía el POST y retorna inmediatamente. El sistema espera un callback asíncrono en un endpoint específico.
- Ventaja: No bloqueamos el servidor mientras el planificador trabaja.
- Desventaja: Mayor complejidad: hay que procesar el callback cuando llegue.
- Justificación: El enunciado es explícito: "el sistema deberá exponer una URL de callback donde el componente externo podrá notificar el resultado de la planificación". Esto implica un flujo asíncrono por diseño. Además, en el mundo real, los algoritmos de planificación de rutas pueden demorar (optimización de rutas, restricciones de capacidad, etc.), por lo que es razonable que tarden y prefieran notificar vía callback en lugar de mantener una conexión HTTP abierta.
- Alternativa 2: Hacer la llamada HTTP al planificador y esperar bloqueante la respuesta con las rutas planificadas.
- Ventaja: Código síncrono más simple de razonar.
- Desventaja: Si el planificador tarda unos minutos en procesar, nuestro servidor queda bloqueado. Además, el enunciado explícitamente pide un callback (el proveedor nos avisa cuando termina), no una respuesta síncrona.

### Reconstrucción de Dominio a partir del JSON del Callback
- El Receiver deserializa el JSON a un `PlanificacionResponse` (DTO) y lo devuelve. La capa de aplicación se encargará de tomar ese DTO y crear los objetos de dominio (`Ruta`, `Entrega`) usando los repositorios. En casos de donaciones que no van a formar parte de una ruta, se devuelve únicamente sus ids.
- A favor: El Receiver tiene una única responsabilidad: deserializar JSON. La creación de dominio queda en la capa de aplicación, donde tiene acceso a los repositorios y puede hacer las validaciones necesarias (ej. verificar que el camión existe, que la donación existe, etc.).
- En contra: Hay que escribir código de mapeo en la capa de aplicación.
- Justificación: Separación de responsabilidades. El Receiver es un componente de infraestructura que sabe de JSON y HTTP. La capa de aplicación es la que sabe de dominio y repositorios. Mezclar estas responsabilidades haría que el Receiver fuera difícil de testear y de mantener.

### Reconstrucción de Dominio desde el JSON del Callback
- Alternativa 1 (Elegida): El Receiver deserializa el JSON a un `PlanificacionResponse` (DTO) y lo devuelve. La capa de aplicación se encarga de tomar ese DTO y crear los objetos de dominio (`Ruta`, `Entrega`) usando los repositorios.
- Ventaja: El Receiver tiene una única responsabilidad: deserializar JSON (Infraestructura). La creación de dominio queda en la capa de aplicación, donde tiene acceso a los repositorios y puede hacer las validaciones necesarias (ej. verificar que el camión y la donación existan).
- Desventaja: Hay que escribir código de mapeo en la capa de aplicación.
- Alternativa 2 (Descartada): Que el `CallbackPlanificadorReceiver` consulte los repositorios y devuelva directamente objetos de dominio (`Ruta`, `Entrega`).
- Ventaja: Menos código de mapeo intermedio.
- Desventaja: El Receiver queda acoplado a la lógica de creación de dominio y a la base de datos/repositorios. Mezcla responsabilidad de deserialización con responsabilidad de dominio.
- Justificación: Separación estricta de responsabilidades (Single Responsibility Principle). El Receiver es un componente de infraestructura que sabe de JSON y HTTP. La capa de aplicación es la que sabe de dominio y repositorios. Mezclar estas responsabilidades haría que el Receiver fuera difícil de testear (requeriría mockear repositorios) y de mantener.

## Repositorios de Logística (Flota y Rutas)
- Se aplicó el concepto de Agregados de Domain-Driven Design para definir los repositorios, evitando crear uno por cada entidad del dominio.
- `Entrega` es una entidad interna que no tiene ciclo de vida propio fuera de una ruta, por lo que se persiste y consulta transitivamente a través de `RutasRepository`.
- `Camion` es la raíz de su propio agregado, y `Posicion` se modela como un Objeto interno.

## Logística - Módulo de entregas y rutas
### Separación entre EstadoEntrega y EstadoDonacion
- Podrían usarse los mismos estados de donación para las entregas, pero modelan máquinas de estados de naturalezas distintas
- EstadoDonacion representa el ciclo de vida del bien (qué le pasó a la silla donada), es un estado del dominio core
- EstadoEntrega representa el ciclo de vida de la acción logística (cómo va el viaje de esa silla), es un estado puramente operativo del módulo de Logística
- Aunque algunos nombres parecieran coincidir, en realidad representan perspectivas distintas
- Además, existen estados logísticos como PLANIFICADA (la ruta existe pero el camión no salió) e INCIDENCIA (alerta temporal de no-recepción) que no tienen equivalente en el dominio del bien
- Se optó por mantener ambas máquinas de estados, pero renombrando EstadoEntrega para evitar la confusión semántica (PLANIFICADA, EN_CAMINO, COMPLETADA, INCIDENCIA)
- La sincronización entre ambos estados se encapsula en la clase Entrega, que actúa como puente entre los dos dominios
- Usar un único enum compartido se descartó porque perdíamos la noción de "ruta planificada pero no iniciada" y la distinción entre "incidencia temporal" y "entrega fallida definitiva", además de acoplar fuertemente el módulo de Logística al Core

### Clase Camion e inmutabilidad parcial
- Se decidió que los atributos físicos del camión (patente, volumen, altura, capacidad) sean final
- Un camión no cambia su capacidad de un día para otro, por lo que garantizar su inmutabilidad previene errores accidentales
- Por el contrario, posicionActual y rutaActual son mutables, ya que representan el estado operativo en tiempo de ejecución
- El método actualizarPosicion() lanza una excepción si el camión no tiene una ruta activa, esto evita que la App móvil del chofer o el GPS sigan enviando coordenadas y ensucien el sistema cuando el camión está apagado en el depósito
- El camión mantiene una referencia a su rutaActual
- Si esta referencia es null (o la ruta está finalizada), el camión se considera disponible para el algoritmo de planificación
- Esto centraliza la lógica de disponibilidad en el propio camión, evitando que la Flota tenga que calcularla externamente

### Clase Ruta y su ciclo de vida
- Se decidió que la Ruta tenga tres estados (PLANIFICADA, EN_CURSO, FINALIZADA) que representan el ciclo de vida operativo del viaje
- Esto es independiente del estado de las entregas individuales, ya que una ruta puede estar "en curso" mientras algunas entregas ya fueron completadas y otras están en camino
- La Ruta mantiene una referencia al Camion asignado, y el Camion mantiene una referencia a su rutaActual
- Esta doble asociación se establece en el método asignarCamion(), donde ambos objetos se sincronizan
- Esto permite que la ruta pueda acceder a la posición actual del camión (para el tracking), el camión sepa si está disponible o no, y al finalizar la ruta, el camión sea liberado automáticamente
- Se decidieron validaciones explícitas para cada transición: no se puede asignar un camión a una ruta que ya está en curso, no se puede iniciar una ruta sin camión asignado o sin entregas, no se pueden agregar entregas a una ruta que ya inició
- Estas validaciones previenen estados inconsistentes y hacen que el sistema sea más robusto
- El método verificarFinalizacion() se encarga de detectar cuándo todas las entregas fueron completadas o están en incidencia, y marca la ruta como FINALIZADA
- Se decidió que la Ruta almacene la fechaDeReparto (el día en que se va a recorrer), no la fecha en que fue planificada
- Esto es más útil para el dominio, ya que el sistema necesita consultar "¿qué rutas hay para hoy?" para el tracking y el dashboard
- La fecha de planificación es un dato de auditoría que no pertenece al dominio de la ruta
- Hacer que la Ruta herede de una clase abstracta "Viaje" se descartó porque no hay evidencia de que vayamos a tener otros tipos de viajes, sería sobreingeniería
- Usar un patrón State para los estados de la Ruta se descartó porque los estados son simples y no tienen comportamiento complejo asociado, un enum es suficiente y más simple

### Clase Entrega como puente entre dominios
- La Entrega representa el acto logístico de llevar una donación a un destino
- Es el puente entre el módulo de Logística y el dominio Core
- Cada transición en la Entrega delega automáticamente un cambio de estado en la DonacionIndependiente subyacente
- Por ejemplo, al iniciarViaje(), la donación pasa a EnTraslado
- Esto garantiza la consistencia entre el modelo logístico y el core del negocio, evitando que queden "desfasadas"
- Siguiendo la aclaración de la cátedra, se decidió que "Incidencia" no es lo mismo que "Entrega Fallida" definitiva, sino un estado de alerta
- Si la donación regresa al depósito, el método reprogramar() resetea la Entrega a PLANIFICADA y la Donacion vuelve a ListaParaEntregar para ser re-planificada en el próximo lote nocturno
- La donacion y el destino son final
- Una vez que el componente externo de rutas planifica la entrega, no puede cambiar a qué entidad va ese bien, solo cambia su estado operativo

## Primeros Endpoints con Javalin (Logística)
- Se decidió utilizar Javalin como framework web para el microservicio de Logística debido a su ligereza, bajo consumo de memoria y sintaxis funcional directa. A diferencia de Spring Boot, no impone una estructura rígida ni trae dependencias innecesarias, lo cual es ideal para un servicio con alcance bien definido y tiempos de respuesta críticos.
- Los primeros endpoints se diseñaron con un enfoque RESTful centrado en la entidad `Camion`:
    - `GET /api/camiones/{patente}`: Devuelve el estado operativo y la posición actual del camión. Si no existe, responde con HTTP 404.
    - `POST /api/camiones/{patente}/posicion`: Recibe las coordenadas y velocidad mediante un DTO (`PosicionRequest`) y delega la actualización al dominio.
- Se separó explícitamente la configuración de rutas en `CamionController` para mantener el `main` de Javalin limpio y cumplir con el Principio de Responsabilidad Única. El controller solo mapea requests/responses y delega la lógica al repositorio/dominio.
- Para los cuerpos de las peticiones, se usó un DTO (`PosicionRequest`) en lugar de parsear manualmente el JSON o exponer la entidad de dominio. Esto evita acoplar la API a la estructura interna de `Camion`, permite validación implícita por el framework y facilita cambios futuros sin tocar el core.
- El manejo de errores se resolvió mediante excepciones de dominio (`CamionSinRutaActivaException`, etc.) que el controller atrapa y transforma en respuestas HTTP semánticas (400, 404, 409). Esto evita bloques `try-catch` dispersos y mantiene la lógica de negocio limpia.
- Por ahora se evita configurar Swagger/OpenAPI, inyección de dependencias compleja o filtros de autenticación. Se prioriza la simplicidad (YAGNI) y la entrega rápida de los endpoints críticos para el tracking. Estas capas de infraestructura se agregarán cuando el servicio crezca o se integre con un API Gateway.

## DonanteRequestDtoBuilder
- Se creó únicamente para poder hacer los tests rápidamente
- Una opción es que conozca una DonanteRequestDto y que lo único que haga es ir llamando a los
los setters de dicha instancia. Pero esto hace que DonanteRequestDto sea MUTABLE, lo cual no es una
necesidad real dado que únicamente se requiere para poder hacer tests. 
- La otra opción es hacer un builder que no conozca la instancia y que al final cree dicha instancia.
Lo que garantiza que DonanteRequestDto sea Inmutable. Por eso es que se decidió implementar esta op-
ción.
- Hay dos formas de implementar la segunda opción:
  - El builder como clase separada
  - El builder como método de la clase DonanteRequestDto (static class)
- El resultado es el mismo, pero se implementó como clase separada para que esté claro qué parte es
de configuración/creación y qué parte es de uso.

## DonanteController vs DonanteHumanoController y DonanteJuridicoController
- La primera opción es más simple porque requiere menos entidades, pero hace que exista un único DTO
para los subtipos de Donantes y que haya que hacer validaciones.
- La segunda es más compleja, pero simplifica las validaciones

## Actualizacion de Atributos
- Hoy por hoy se asumen los atributos como obligatorios en general o, mejor dicho, no nullables.
- Si surgieran atributos Nullables, entonces una opción es Reificar los atributos para que la respon-
sabilidad de actualizarse la tengan ellos. Por ahora, por YAGNI se va a dejar así.

```java
public class Obligatoria<T> implements Propiedad<T> {
    private final T valor;
    private final String nombre;

    // constructor...

    @Override
    public Propiedad<T> actualizar(T nuevoValor, boolean parcial) {
        if (nuevoValor == null) {
            if (parcial) return this; // En parcial, si es null, se ignora
            throw new DatosInvalidosException(nombre + " es obligatorio");
        }
        return new Obligatoria<>(nuevoValor, nombre);
    }
}

public class Opcional<T> implements Propiedad<T> {
    private final T valor;

    // constructor...

    @Override
    public Propiedad<T> actualizar(T nuevoValor, boolean parcial) {
        // Si es parcial y no viene nada, mantenemos el viejo. 
        // Si viene un null explícito o un valor, lo cambiamos (permite borrar).
        if (parcial && nuevoValor == null) return this; 
        return new Opcional<>(nuevoValor);
    }
}
```

## RequestDTO vs ResponseDTO
- Existen, a pesar de que compartan casi todos los atributos y demás, porque representan dos ideas
totalmente distintas.
- Además, la herencia no tiene sentido porque genera acomplamiento a la hora de instanciar Response
y no tiene sentido plantear que uno es el subtipo del otro porque NUNCA vas a esperar poder usar a
ResponseDTO en todos los lugares donde esperas usar RequestDTO.

## ¿Se puede evitar el casteo en ResponseDTO?
- Se puede si haces que Donante implemente la interfaz DatosDonante, lo cual hace que puedas usar 
polimórficamente ambos subtipos.
- Lo que tiene este enfoque es que hace que sea más extensible el DTO porque ante nuevos cambios o
si surge un tercer tipo de Donante, entonces no va a haber que modifcar el DTO y en tiempo de compi-
lacion va a haber que tocar Donante para que implemente los nuevos métodos.
- Lo malo es que vuelve menos cohesivo DONANTE o los subtipos de DONANTE porque hace que tengan ex-
pongan métodos que representan datos que no se relacionan con ellos.
- [ESTO ES UNA BUENA PREGUNTA]

## ¿Está mal dejar que JACKSON genere los JSONs directamente usando las entidades del dominio?
- En el microservicio de donaciones se dedició en general generar los DTOs en lugar de permtir que
Jackson los infiriera a partir de las entidades del dominio debido a que:
  - posibilita tener validaciones al crear instancias.
  - permite deserializar jerarquías polimórficas sin "ensuciar" las entidades de dominio con anota-
  ciones relacionadas a Jackson, lo cual genera desacoplamiento entre el DOMINIO y la herramienta que
  se usa para serializar/deserializar el dominio.
  - da flexibilidad para "mostrar" lo que uno quiere y no necesariamente aquello que es público en
  la jerarquía.
  - el contrato HTTP puede evolucionar de forma diferida al dominio, por ende de esta forma un cambio
  en el dominio no necesariamente rompe el contrato.

## ¿Está mal que las entidades de dominio se sepan "refrescar"?
- Si bien a primera vista pareciera que hay un problema de cohesión porque son entidades que tienen
la lógica de negocio y estarían incorporando lógica relacionada de alguna forma a la persistencia, la
realidad es que esa operación sólo está modificando estado interno, no está manejando realmente lógica
de persistencia perse.
- Además, te evita tener que:
  - exponer setters para que alguien "actualice" desde afuera.
  - poner por afuera la lógica o decisión de qué hacer si el atributo es obligatorio o parcial, lo
  cual evita exponer información estructural de la entidad.
  - ir en contra del "tell don´t ask"
  - duplicar la lógica de actualización si, en el futuro, surge una nueva forma de actualización dis
  tinta a la de request HTTP (de hecho ya pasa con los archivos CSVs).
- Usar `datosDonante` o, de forma más general, `datosEntidad` desacopla al dominio de aquello que lo
usa para actualizar al mismo. 
- El dominio define que necesita algo que pueda brindar la información
necesaria para poder actualizarse y son otros quienes lo llaman brindándole aquello.

## Builder Necesidades y Entidades beneficiarias
- Ambos se crearon, de forma consistente con decisiones previas, para facilitar el testing y para
separar los concerns de Configuración y Uso.

## Factory en Necesidad o entidad de dominio vs DTOs
- Mantenerlo a nivel de dominio permite que no esté acoplado o únicamente al nivel la capa web y que
sea reutilizable.

## DatosDonante/DatosNecesidad/DatosEntidadesBeneficiarias
- Existen para que la lógica de actualización de las intancias reales del dominio no dependa de
cosas externas al mismo. Es decir, el dominio NO depende de la capa web.
- El dominio exige una interfaz y la capa web la cumple.
- El dominio nunca se entera de dónde viene esa interfaz o quién la está cumpliendo

## Repository
- Todas los repositorios tienen una base común para las operaciones CRUD y, por ende, se definió la
clase abstracta `Repository` que hace que sea fácilmente extensible la idea.

## Persistible distinto de Actualizable
- Se los separa por una cuestión semántica y de cohesión, ya que la primera apunta a la identidad y
la segunda a la posibilidad de ser actualizable.
- Unir ambas ideas es más simple en cuanto a cantidad de entidades, pero hace que esas ideas estén
acopladas y que sea menos flexible ante casos de entidades con identidad pero sin la necesidad de
ser actualizables y viceversa.

## NecesidadesRepository
- Se decidió que no exista como tal, ya que las necesidades sólo tienen sentido en relación a una
entidad beneficiaria.
- Cada entidad beneficiaria tiene su propio "repository de necesidades".
- Lo que pasa es que hoy los repositories son el "acceso" a la BD, pero como no hay una base de datos
y su implementación es con una lista en memoria, entonces se da que las necesidades se pueden plantear
como un repository.
- Más adelante, cuando surja la Persistencia, probablemente la abstracción `repository` cambie y de
lugar a `repositoryEnMemoria` vs `repositoryConPersistencia` o algo así.

## Eliminable
- Sirve para hacer soft delete de las entidades persistibles que lo requieran.
- Es importante para los casos donde hay otra entidad que guarda una referencia a aquello que se va 
a eliminar y que, a pesar de la eliminación, la otra entidad debe seguir siendo válida.
- Por ahora, las entidades que tienen sentido que sean Eliminables son:
  - Donante (lo referencia DonacionIndependiente)
  - Entidad Beneficiaria (la referencia Donacion Independiente)
  - DonacionIndependiente (por trazabilidad)
- Necesidad no es Eliminable porque es composición pura

## Notificable como interfaz
- Se decidió no acoplar la idea de Notificable a Persistible y, como en Java no hay herencia
múltiple, entonces se reificó como interfaz.

## Medios de Notificacion como Enums
- Cuando se empezó a querer construir las instancias, el hecho de que los Notificables conocieran
un Medio real era incómodo. Así que se simplificaron las instancias de dominio y utiliza un factory
para obtener la instancia real a partir del enum.
- Entonces, para esto se define un `BusNotificaciones` que se encarga de enviar la notificación por
el medio correcto.
- Quizá `BusNotificaciones` roza el Service, pero realmente sino las Notificaciones dejan de ser
cohesivas. Es decir, pasan a tener demasiadas responsabilidades y muy variadas.

## ¿Enviar de medio de notificacion con lógica o vacío?
- Sabes que SIEMPRE vas a llegar al Medio, independientemente de si pasas por el bus, sólo por eso
dejaría la lógica, aunque complejice la entidad MedioDeNotificacion, garantiza consistencia a la hora
de enviar notificaciones.

## Medio de Notificacion Factory: Switch vs Diccionario
- El switch es más simple y te da seguridad de tipado en compilación, pero es menos flexible y más
dificil de testear.
- El diccionario no te da seguridad de tipado en compilación y es más complejo, pero es más flexible
y fácil de testear.
- Simplemente porque no tengo intención de hacer modificaciones en runtime ni se espera agregar más
medios, sumado a que el stubbing no es muy dificil, elijo el switch.

## Solicitud de asignación vs Solicitud de revisión
- Solicitud de asignación tiene sentido porque:
  - Calcular el ranking es cotoso y no tiene sentido recalcularlo cada vez que alguien consulta.
  - Audita qué entidades fueron propuestas, además de la elección.
  - Las `candidatas` son un dato que no existe en otro lugar del sistema.
- Solicitud de revisión no tiene sentido, además de que no escala generar un nuevo repo por cada
solicitud que se necesite crear, porque:
  - Las opciones son siempre las mismas (`Vencida`/`EnDeposito`).
  - La decisión que hay que tomar es lo que ya `resuelve validarTranisicion`.
  - Ya existe todo lo necesario para resolver el requerimiento:
    - Estado `EntregaFallida`
    - Transiciones de estado: los endpoints
    - Ya se notifica a los admins de la situación

## ¿Cuándo recibe una entidad beneficiaria una donacion?
- Actualmente, esto es sinónimo de que se le asigne una donación. Sin embargo, con el flujo de que
, a pesar de ser asignada, no necesariamente se la entrega y eso no significa un reintento de enviar-
sela lo que realmente pasa es que recién la recibe cuando se la entrega y no cuando se la asigna.
- Hay 2 posibilidades:
  - No recibirla cuando se la asgina, sino cuando recibe la entrega (es ejecutar el mismo método,
  pero en otro momento).
  - Dejar todo como está y simplemente tener una forma de eliminar una `donación recibida` de una en-
  tidad beneficiaria.
- La primera pareciera ser semánticamente más fiel y ya están todas las herraientas para que esto
suceda, por ende se la elige por sobre la otra.

## Pendiente
- ¿Es entregaFallida o es otro estado o una flag?
- Realmente que sea otro estado por ahora no suma mucho, además hay que hacer esa transición.
- Quizá que sea otro estado tiene sentido por trazabilidad.
- Por ahora se dejó como EntregaFallida y se permite transicionar desde EF a Vencida.
- Para la lógica de esto lo que pasa es que hay un endpoint que te trae todas las pendientes y después
desde el front o cualquier lado le pueden mandar al endpoint en_deposito o vencer.
- Si hubiera que agregar otro estado se podría definir una tarea programada que cada cierto tiempo
pase las EntregaFallida a Pendiente o simplemente se puede hacer ambas transiciones juntas.

## Endpoint /health
- Mostramos la hora local en UTC / Zulu time.


# Persistencia |

## Donaciones-Service

### Actualizable
- Es una interfaz que ninguna entidad que se persista usa de forma polimórfica, por ende no se debe convertir a clase abstracta ni mucho menos.

### Eliminable
- Es una interfaz que ninguna entidad que se persista usa de forma polimórfica, por ende no se debe convertir a clase abstracta ni mucho menos.

### Persistible
- Se cambió el nombre del atributo `id` por `idDominio` sólo para dejar en claro que no es el id de JPA
- Probablemente cambiar el nombre de la clase sea una buena idea porque puede confundir. Si bien todo lo que herede de `Persistible` va a ser persistible en términos de JPA, la realidad es que si tuvieras algún mapeo por Table Per Class, que no pueda heredar de Persistible porque su Generated Value tiene que ser por Secuencia, entonces ahí habría algo persistible en el dominio de JPA, pero no en términos de nuestro dominio.

### Necesidades
- En el sistema se usa polimórficamente a las Necesidades el 100% de las veces y nunca se accede a cada subtipo de Necesidad por separado, esto hace que, en términos de mapeo de herencia, la estrategia de TablePerClass no tenga sentido.
- Mapear la herencia por Joined es una posibilidad, pero no estamos ante un caso donde cada subtipo tiene muchos atributos específicos que justifique tener 1 Tabla para Necesidad + 1 Tabla para NecesidadRecurrente + 1 Tabla para NecesidadExtraordinaria.
- Realmente, los subitipos no tienen una gran cantidad de atributos específicos (las NecesidadesExtraordinarias no agregan atributos y las Recurrentes agregan sólo 2).
- Se elige mapear la herencia usando la estrategia SingleTable:
  - Tamaño de tabla manejable, no hay muchos atributos. Aún agregando el discriminador sigue siendo de un tamaño razonable.
  - En el peor de los casos la cantidad de campos que quedarían en null son 2 y en el mejor 0
  - La performance de las consultas es mucho mayor, dado que toda la información (que no sean relaciones) está en una sola tabla.

### TipoNecesidad (enum)
- No se persiste, ya que las Necesidades no tienen un atributo que referencia al Tipo; pero, dada la estrategia de persistencia elegida, aparecerá en la Tabla un discriminador que denota esta misma información.

### Relación: Necesidad --> Bien Requerido
- ¿Es una relación @OneToOne, @ManyToOne o está Embebido?
- En el sistema a los Bienes no se los trata como algo con identidad, es decir no se hacen operaciones donde se tenga que diferenciar a un Bien de otro (por identidad) y siempre se está creando un Nuevo Bien cuando se asigna a una entidad, entonces siempre se lo trata como un Valor y no como una entidad.
- Entiendo que, aún estando embebido, puede relacionarse con la SubCategoría como una Relación.

### SubCategoria: ¿es una entidad o un valor?
- La respuesta está en el método: `esLaMismaSubCategoria(SubCategoria otraSubCategoria)`.
```java
public Boolean esLaMismaSubCategoria(SubCategoria otraSubCategoria){
    return this.nombre.equals(otraSubCategoria.getNombre()) && this.categoria.esLaMisma(otraSubCategoria.categoria);
  }
```
- Previo a la existencia de la persistencia y, por ende, de la perdida de la identidad en Objetos por tener que congeniar a Objetos con Relacional, ya **NO** se usaba la `Identidad` para comparar dos Subcategorías, con lo cual la misma **NUNCA** importó.

### Categoria: ¿es una entidad o un valor?'
- Nuevamente, por cómo se utilizaba antes de la existencia de la persistencia: es un **VALOR**.
- El problema es que hay una jerarquía polimórfica dada a través de una interfaz y eso sólo se puede persistir a través de mapeo de herencia y el mapeo de herencia implica que `Categoria` es una **Entidad**.
- No se puede plantear un enum para no tener que mapear herencia porque Perecedero tiene estado interno: la `fechaDeVencimiento`
- Plantearlas como entidad hace que:
  - hayan muchos registros con valores potencialmente iguales cuando lo que te importa únicamente son esos valores y no la identidad
  - si quisieras evitar que haya muchos registros con valores potencialmente iguales tengas que poner algún tipo de constraint a nivel base de datos, como que sea **UNIQUE** el estado de uso y la fecha de vencimiento, imposibilitando el uso de la estrategia de mapeo de herencia `SingleTable` y, que por el tipo de consultas que se hacen, sólo se pueda usar `Joined`
  - si no se ponen los constraints a nivel base de datos y se usa `SingleTable` la operación de registrar una Categoría sería mucho más costosa (si se quiere respetar que no se creen Categorías con mismos valores)
  - complejizar la consulta y hacerla más costosa (1 SELECT vs SELECT + JOIN)
  - si queres reutilizar registros --> SELECT extra antes de cada INSERT + condiciones poteciales de carrera
  - ciclo de vida --> se complejiza eliminar tanto Bienes como Categorías
- Aplanar los atributos en SubCategoría:
  - la SubCategoría recibe en el constructor una Categoría y según el `TIPO` completa ciertos campos de SubCategoría, que pasaría a tener información duplicada de Categoría; pero sin sus métodos:
  - imitaría, a nivel objetos, un mapeo por SingleTable; pero sin tener que hacerlo
  - lo que **NO** me gusta de esta alternativa es que modela Objetos pensando en Relacional
  - Otra forma sería que no exista Categoría y que, en SubCategoría estén todos los atributos de una y que haya validaciones para no poder setear combinaciones inválidas.
  - Se lo dejó de una forma que "castiga" al modelo de objetos, pero que no pierde el concepto de `Categoría` ni trae apareajados los problemas a nivel relacional de la otra solución.

### Recibo de donación: ¿entidad o valor?
- ¿Cada recibo tiene una identidad intrínseca? La respuesta es que, en teoría, si vos comparas dos recibos por la donación a la que hacen referencia podes saber si son el mismo o no.
- Lo cierto es que no hay una regla que no permita que crear dos recibos de donaciones distintos con la misma donación independiente, pero por como funciona en el modelo su creación y demás, nunca va a ocurrir
- Algo a tener en cuenta es que no es que las Necesidades tienen 1 recibo que se va modificando, cada Recibo es inmutable.
- No tienen un ciclo de vida propio, están completamente ligados al funcionamiento de Necesidad.
- Con todo eso en mente, se puede afirmar que, en nuestro dominio, son **VALORES**

### Necesidad --> Recibo de Donación
- Se convirtió en un SET, dado que no interesa que haya **repetidos** ni importa el **orden**
- Se escribieron las anotaciones @ElementCollection y @CollectionTable porque es una relación de OneToMany con elementos embebibles.
- Algo no relacionado a la persistencia, se corrigió el código de la clase porque había un `Template Method` a medio camino para generar las **Donaciones Recibidas Actuales**

### Donacion Independiente: Fecha de Donación [CALCULADO]
- Antes se seteaba al crear la instancia, pero se detectó que, ni bien se creaba, también se agregaba un registro al historial de cambios de estado. Con eso en mente, la fecha podía dejar de ser un atributo y pasar a ser algo calculado.
- Si bien esto puede ser menos performante, todavía no hay un registro real de que esto sea así. No tiene sentido pensar en optimizar antes de que haya una necesidad real.
- Facilita no tener que mantener la consistencia entre dos fuentes de verdad: atributo/campo de Donación independiente y el historial de estados.

### Donación Independiente: el `Historial de Estados` es una Lista, un Set o un Bag?
- Realmente **NO** interesa el orden, entonces puede no ser una `Lista`
- Tampoco tiene sentido que haya duplicados, entonces es un `Set`
- El problema del SET es que hay tests donde las transiciones se hacen de forma instantánea, que es un comportamiento no real, y LocalDateTime deja de ser útil para compararlas porque se crean con el mismo valor.
- Por eso mismo lo dejamos como List que nos garantiza un orden más allá de la fecha de transición, lo cual se paga teniendo que agregar una columna más en la base de datos `orden`

### Donación Independiente: el `Historial de estados` es relación OneToMany o un ElementCollection de Embebibles?
- La clave es pensar si `CambioDeEstado` es un embebible o una entidad. La forma de determinarlo es igual a la de `ReciboDeDonación`:
  - No me importa realmente la identidad, lo único que me importa es el estado interno.
  - Si hay dos donaciones independientes con la misma transición en el mismo momento es puramente casualidad y no tiene sentido o no se gana nada haciéndo que apunten al mismo
  - El ciclo de vida está supeditado a la Donación independiente
  - No se muta en ningún momento
- Por ende, lo definimos como **VALOR**.
- En consecuencia, el `Historial de estados` es un `ElementCollection` de embebibles.

### Cambio de Estado: Record -> Clase
- Lamentablemente, con la versión que usamos de Hibernate no se puede embeber Records. Por ende, hubo que convertirla a una clase normal.

### EstadoDonación: clase abstracta o enum?
- La realidad es que si es una clase abstracta hay que tratarlos como una entidad para poder mapear la herencia y eso genera problemas como tener valores duplicados que expresan lo mismo o tener que agregar restricciones que obliguen al agregar un estado a la base tener que verificar que no exista previamente y demás.
- Si es un enum tiene como problema que se vuelve bastante grande, pero esa complicación es superada con creces al no tener que mapear herencia ni declarar como entidad a los estados.
- Por esto, determinamos que es un enum.
- Sería un enum si EntregaFallida no tuviese estado, pero como tiene estado hace que ya no tenga sentido modelarlo como un enum y, por ende, hay que pensar en una estrategia de mapeo de herencia. De hecho, en este caso, justo para la entrega fallida, el estado interno podría ser el mismo y eso no significaría que representan la misma instancia.
- Una forma de resolverlo sería sacar la Justificación de EntregaFallida y ponerla en Donación, de forma que pasen a ser un Enum y no haya que mapear herencia:
  - Te da consultas y un modelo más simples
  - Los enums no tienen problemas con la identidad
  - Tenes la columna `justificacion` en null mucho tiempo (esto es lo negativo)
  
### Donación Independiente: Entidad Beneficiaria
- ¿Es una relación bidireccional? El campo de la Donación Independiente refleja la `Asignación`, mientras que la relación que se plasma a nivel Entidad Beneficiaria (Necesidades -> Donaciones) refleja otra relación que es la `Entrega`. Por ende, **NO** son la misma relación y, por eso, la mantenemos.
- ¿Es necesario tenerlo como campo? Se puede deducir del último `CambioDeEstado`, por ende se elimina el atributo porque genera dos fuentes de verdad que hay que mantener consistentes.

### EntidadBeneficiaria:
- Es una **Entidad**.
- La mayoría de atributos son básicos, es decir se mapean directo.
- Las Necesidades se mantienen en Transient porque por cómo están hechas no es posible mapearlas como se debe, es decir primero hay que sacar el repository y dejar una lista.

### EntidadBeneficiaria-Necesidades
- Es una relación OneToMany porque cada Necesidad se relaciona con una única EntidadBeneficiaria y cada EntidadBeneficiaria se relacion con muchas Necesidades.

### CambioDeEstado-EntidadBeneficiaria
- Es una relación ManyToOne porque a nivel objetos el Cambio conoce una única EntidadBeneficiaria (referencia simple) y a nivel relacional una EntidadBeneficiaria puede conocer muchos Cambios, ya que se le pueden asignar muchas donaciones a lo largo del tiempo.

### Solicitud de Asignacion
- Es una entidad

### SolicitudDeAsignacion -> EntidadBeneficiaria elegida
- Es una relación @...ToOne
- A nivel relacional una EntidadBeneficiaria se puede relacionar con  muchas solicitudes de asignación porque puede ser elegida varias veces distintas como candidata a recibir una donación.
- Entonces, es una @ManyToOne

### SolicitudDeAsignación -> EntidadesBeneficiarias candidatas
- Es una relación @...ToMany
- A nivel relacional una EntidadBeneficiaria puede ser candidata de muchas solicitudes de asignación a lo largo del tiempo.
- Entonces, es una @ManyToMany

### SolicitudDeAsignacion -> DonaciónIndependiente
- Es una relación @...ToOne
- A nivel relacional una Donación puede relacionarse con más de una Solicitud de Asignación dado que su entrega puede fallar y ahí vuelve a `EN_DEPOSITO` y el proceso se reinicia.

### Administrador: Entidad
- Es una entidad

### TipoMedioDeContacto
- Al ser un enum todas las entidades lo van a mapear como Enumerated

### Contacto: Entidad o Valor?
- Al ser mutable, es una entidad.

### Administrador-Contacto
- Es una relación @...ToOne
- Ahora bien, un Contacto sólo tiene sentido que se relacione con un Administrador y nada más que con uno.
- Entonces, es @OneToOne
- Se le agrega el CASCADE porque el Contacto está totalmente atado al Adminitrador en cuanto al ciclo de vida.

### Notificacion
- Es una Entidad

### Notificacion: NotificacionFija, NotificacionPreferencial y Mapeo de Herencia
- El supertipo tiene una gran cantidad de atributos y únicamente NotificacionFija provee un único atributo particular. Por ende, `SingleTable` es la estrategia que optimiza las consultas (no requeris Joins ni Unions).
- Además, no se desperdiciaría memoria porque, a lo sumo, hay registros que tienen un campo en null
- Lo único raro es que el valor que queda en null es `tipo_de_medio` y puede ser confuso; por eso se lo nomencló como `tipo_de_medio_fijo`.
- También se podría mejorar si se crean 3 tipos de NotificacionesFijas: NotificacionMail, NotificacionSms y NotificacionTelegram y ahí no tendrías ese valor nulo; pero tendrías duplicada la jerarquía de `MedioDeNotificacion`.

### Notificación: Notificable
- Se lo marcó como Transient por el momento

### Notificacion: TipoDeMedio o MedioDeNotificacion?
- Tranquilamente se podría con un PostLoad usar setear el Medio usando un ServiceLocator o al receptor (dependiendo de la Notificacion).
- Como no cambia demasiado, se mantiene, aunque sea por ahora, el enum y ya.

### Usuario hereda de Persistible
- Esto es una herencia del pasado, valga la redundancia, que se había hecho `posiblemente` para generar una jerarquía acorde en una tecnología que no permite herencia múltiple.
- Como no tiene mucho sentido se replanteo todo y se cambió Usuario por la interfaz `ConSesion` y se rompió esa herencia **forzada**.

### Entidades que heredan de Persistible e implementan Notificable
- Como Notificaciones **CONOCE** a su receptor, que es `Notificable`, y a las mismas se las persiste, sabiendo que en JPA/HIBERNATE las interfaces **NO** se persisten, hay que pensar en transforma `Notificable` a una clase abstracta y hacer un mapeo de herencia.
- Implicaría que Administrador/EntidadBeneficiaria/Donante que heredan de Persistible deban heredar de otra clase (**NO** hay herencia múltiple).

### ¿Hay alguna forma escapar de la Herencia Múltiple?
- Sí, hay varias maneras:
  - Que no hereden de Persistible:
    - Tendrías "código" duplicado relacionado a Persistible (lo cual no es muy grave con las herramientas de hoy en día que haces CTRL+F y encontras fácilmente lo que buscas).
    - Perderías semáncticamente la idea de que esas clases son Persistibles, esa idea deja de estar documentada (en el código y en los diagramas).
  - Que Notificable herede de Persistible:
    - Como las Notificaciones son Persistibles, entonces aquello que es Notificable también lo debería ser.
    - Es relación conveniente, pero que es casi implícita:
      - Si las Notificaciones dejaran de ser persistibles, los Notificables no dejarían de serlo automáticamente.
      - Tampoco es `natural`
    - Implicaría pensar en un mapeo de herencia de 3 clases que no comparten prácticamente nada:
      - SingleTable:
        - Habría bastantes campos y varios de ellos podrían ser Null
        - Si siguiera creciendo la jerarquía de Notificable se volvería aún más inmanejable
        - Muchas consultas no se benefician de tener las 3 clases juntas en una tabla porque no apuntan a tratar a las 3 de forma polimórfica
      - Joined:
        - N + 1 Tablas
        - Todas las consultas implican un Joined y, como ya dije, muchas de las consultas no están pensadas para tratar polimórficamente a las 3 clases [Joined es una operación pesada]
        - Mejora el problema de los campos null porque cada subtabla tendría los campos particulares no compartidos
      - TablePerClass:
        - Hay muchas consultas que **NO** tratan a las clases polimórficamente, con lo cual tener tres tablas separadas podría ser más performante.
        - El reintento de envio de notificaciones es una tarea que se hace bastante seguido, entonces puede que sea una consulta bastante común que al tener un table per class que supone "N" Unions sea pooc performante respecto a las otra alternativas.
  - Que las Notificaciones **NO** conozcan a un `Notificable`, sino a un **CONTACTO** o como se lo quiera llamar y que las otras clases: Donante, Administrador y EntidadBeneficiaria, conozcan un Contacto:
    - Te evita plantear un mapeo de herencia entre las tres clases.
    - Unificas la forma de representar el MedioDeContacto de las tres clases que, hasta el momento, se representaba de tres formas distintas.

### Contacto: ¿es una entidad o un valor?
- **DEPENDE !!**
- Si se hace a la clase inmutable, es decir cuando se quiera mutar a cierto atributo (mail o teléfono) se reemplaza por un Contacto **Nuevo** haciendo uso de `call and return`, en lugar de `shared memory`; entonces podría ser un valor.
- Si se lo plantea como algo **MUTABLE**, entonces es una entidad.

### Contacto como valor
- Permitiría embeberlo en la Notificación sin generar una tabla.
- El contacto que figuraría en la Tabla de Notificaciones representaría al Contacto al que se envió la Notificación; mientras que el que figuraría en las tablas de Donantes, Administradores y EntidadesBeneficiarias sería el Contacto Actual.
- El reintento de envio de notificaciones supondría dos cosas (mutuamente excluyentes):
  - Siempre se envian las notificaciones al mismo contacto:
    - Se enviarían al mismo mail/teléfono
    - Las notificaciones preferenciales ya no se enviarían la medio preferencial actual; sino que se enviarían al medio preferencial del momento en el que se creó la Notificación
  - Se tendría que buscar la Notificación y al Notificable asociado a la Notificación para traer el Contacto actualizado

### Contacto como entidad
- **No** se podría embeber
- **No** habría representaciones de Contacto distintas. Las Notificaciones se relacionarían con el Contacto de un Notificable y un Notificable se relacionaría sólo con un Contacto, por ende si se modifica las Notificaciones tendrían siempre la información actualizada.
- El reintento de notificaciones **SIEMPRE** tendrían la información actualizada en la Notificación.
- Si importara a qué mail/s y/o teléfono se envió la notificación en determinado momento se podría:
  - Generar un historial de cambios en el contacto que y agregar un campo que sea fecha de envió de la Notificación y de esa forma podrías obtener la información
  - Si te importara conocer el registro de cada reintento, entonces podrías hacer que cada Notificación tenga un array de contactos que representen los registros del historial del Contacto con los que se relacionó en cada reintento y eso generaría una tabla notificacion_reintento_contacto.

### Contacto: mails
- Como, por ahora, se los modela como strings se pueden mapear como @ElementCollection y ya.

### ConMailIndividual
- Sirve para no tener que reescribir los métodos iguales entre Donante y Administrador sin tener que gastar la herencia en algo que son sólo getters/setters.

### Notificaciones: DatosDeContacto
- Es una relación `@...ToOne` porque una Notificación sólo se relaciona con unos DatosDeContacto, con la salvedad de que se quiera saber exactamente a qué email/teléfono se envió la notificación
- Ahora bien, cada DatosDeContacto se puede relacionar con muchas Notificaciones porque puede recibir muchas Notificaciones.

### DatosDeContacto: Administrador | EntidadBeneficiaria | Donante
- Es una relación `@...ToOne` porque cada una de esas entidades de relaciona, a nivel objetos, con un DatosDeContacto
- Termina siendo un `@OneToOne` porque cada DatosDeContacto tiene **IDENTIDAD** y **MUTA** acompañando el ciclo de vida de la EntidadBeneficiaria, Administrador o Donante a la que esté relacionada.
- Como **SIEMPRE** se piensa la creación de un Donante|EntidadBeneficiaria|Administrador con el Contacto, se usa el `CascadeType.Persist` para no tener que estar persistiendo primero los DatosDeContacto y luego, recién, a la otra entidad.

### Donante
- Es una entidad.
- Hay una jerarquía: Donante -> PersonaHumana | PersonaJurídica que debe ser mapeada como herencia.
- Los atributos a persistir son **TODOS** fácilmente mapeables, no tiene mucha ciencia explicarlos.
- Los únicos temas a tratar son: Mapeo de Herencia | NombreCompleto y DatosDeContacto (ya fue tratado anteriormente).

### NombreCompleto
- Se eliminó y se flateó a nombre y apellido por separado.
- Tenerlo en conjunto sólo reducía en 1 la cantidad de parámetros que espera DonanteHumano y no surgió comportamiento para poner en esa abstacción.
- Se simplifica a nivel de entidades y de persistencia.
- Se complejiza un poco la interfaz de DonanteHumano.

### Donante: Mapeo de Herencia
- No existen consultas, por lo menos por ahora, que diferencien DonanteHumano de Jurídico. Es decir, siempre se "entra" por el supertipo, con lo cual `Table_per_class` queda descartada porque para las consultas que se hacen es la menos óptima (Unions)
- Esto nos deja con 2 estrategias:
  - Single_Table:
    - Donante provee 4 atributos (compartidos)
    - PersonaHumana provee 6 atributos
    - PersonaJurídica provee 4 atributos
    - En total quedaría una tabla de 14 atributos más el valor para diferenciar los subtipos, es decir 15.
    - Potencialmente podes tener varios registros con 6 atributos nulos o con 4 nulos, lo cual puede ser, a nivel de espacio, un déficit respecto a lo que te puede proveer Joined.
    - Como ventaja las consultas se resuelven en una única tabla, con lo cual son más performantes.
    - Perdemos constraints `not null` de algunos campos, pero lo podemos manejar y, de hecho, lo hacemos a nivel dominio. No existe otro sistema que nos popule la base de datos, por ende esto no es un problema.
  - Joined
    - **No** necesita un discriminador para los subtipos
    - No van a existir registros con campos nulos, salvo que algún campo sea nullable.
    - Se necesitan 3 tablas.
    - Las operaciones son más costosas porque implican, al menos, 1 Joined
    - Insertar implica 2 inserts
    - Está más normalizada
- Para la cantidad de datos que manejamos y, por las consultas que hacemos, realmente prefiero ser más costoso en cuanto a memoria; pero más performante a nivel consultas.

### DonacionIndependiente-Donante:
- Es una relación `@...ToOne`, ya que cada Donación se relaciona con su Donante.
- Termina siendo `@ManyToOne` porque un Donante puede **donar** más de 1 vez, con lo cual se puede relacionar con muchas donaciones.

### Publicador de Cambios de Estado y Observers
- Se podrían persistir ambos y con un ServiceLocator configurar correctamente los observers, pero implica todo un mapeo de herencia (complejo) para poder hacer algo que nuestro sistema no lo requiere.
- Nosotros desde que empieza hasta que termina el programa queremos los mismos observers para todas las donaciones y no es un requerimiento real que vaya a pasar de que haya donaciones con algunos observers y otras con otros.
- Nuestro sistema crea las donaciones independientes mediante el repository de donaciones cuando se recibe una donación, por ende ahí es cuando se enchufa el publicador con los observers deseados y, por eso, es que todas las donaciones independientes van a estar configuradas de la misma manera.
- De hecho, **NO** permitimos la suscripción y desuscripción a donaciones de forma particular.
- Se decidió ir por un camino intermedio:
  - No lo vamos a mantener como estado global (para no tener que acordarse de desuscribir todos los observers en los momentos que no nos interesa)
  - No vamos a mapear la persistencia del Publicador y los interesados (mapeo de herencia + service locator para configuracion PostLoad)
  - Se va usar el PostLoad para enchufar el Publicador de eventos que se haya seteado para todas las donaciones en el ServiceLocator:
    - Te ahorra los problemas de acordarte del estado compartido
    - Hay que acordarse de configurar el PublicadorDeEventos en el ServiceLocator (esto hace que en los tests esté la misma idea de configuración previa que con los Main de configuración)
    - **NO** tenes la idea de configuración individual (YAGNI)

### SoftDelete
- Se podría usar un timestamp en lugar de true/false para tener un cron que limpie los registros después de cierto tiempo de eliminados.
- Te da:
  - Recuperación ante errores
  - Auditoría
  - Integridad referencial
- Tener cuidado con:
  - Privacidad de lxs usuaries: derecho al olvido --> Eliminación después de "X" tiempo de eliminado o inmediato
  - Crecimiento desmedido de la base de datos --> cron 
  - Unique constraints --> filtrar por eliminado --> lo regulamos desde el modelo de objetos entendiendo que no hay otro sistema que cargue datos y que las condiciones de carrera son mínimas.
  
### Filtros que sepan mapear su consulta a SQL
- Haciendo esto se podría reutilizar lógica y no recurrir a métodos de consulta particulares
- El tema es que puede llevar tiempo y agrega complejidad (es sólo repetir un poco de código vs crear una serie de filtros polimórificos, etc)

### Se eliminó como criterio de Unicidad de Donante
- Puede pasar que haya casos, muy improbables, de que se repitan

### NotificacionesRepository
- No se lo crea porque todas las consultas SIEMPRE son en relación a una `EntidadBeneficiaria`

### DonacionIndependiente: estadoActual
- En un principio estaba desnormalizado y como se podía calcular mediante el historial de estados se lo **normalizó**.
- Cuando se implementó IDonacionesRepository se hizo evidente que había que implementar los siguientes métodos:
```java
  List<DonacionIndependiente> getListasParaEntregar();

  List<DonacionIndependiente> getAsignadas();
  
  List<DonacionIndependiente> getPendientesDeRevision();
  
  List<DonacionIndependiente> recibirDonacion(Donacion nuevaDonacion);

  List<DonacionIndependiente> getDonacionesEnDeposito();
```
- Si estuviera **normalizado** habría que hacer:
  - Hacer un `JOIN` con la tabla Historial_Cambios_de_Estado
  - Agrupar o usar una subconsulta para encontrar el registro con la fecha más reciente (o el mayor orden_de_transicion) para cada donación
  - Filtrar si ese último registro coincide con el estado buscado
- Estando **desnormalizado** se resuelve por una consulta directa a la tabla de Donaciones
- Encima la consistencia entre estadoActual y el historial de estados está dado porque:
  - Sólo nuestro Sistema modifica la base de datos
  - Todas las modificaciones provienen del modelo de dominio donde los métodos que modifican el estado setean el nuevo estado y agregan un CambioDeEstado al historial

### EnDeposito -> AsignacionRealizada
- Originalmente, por enunciado, esa transición era válida, pero para nuestro sistema no tiene sentido que lo sea, ya que la idea es que se revise y se genere la solicitud de asignacion.

### PublicadorDeCambiosDeEstado
- Para mí es poco cohesivo que esté en el repositorio de donaciones
- Lo pondría en un serviceLocator y que se acceda desde ahí o que esté inyectado en la Donacion:
  - Las Donaciones son cosas del dominio que siempre van a venir de afuera, entonces se las puede configurar desde el DTO fácilmente.
- Sólo por una cuestión de tiempos, por ahora, se mantiene en el repository.

### CrearSolicitudesDeAsignación: proceso **BATCH**
- Se encontró un "bug" que es que antes con este código:
```java
public List<SolicitudDeAsignacion> ejecutar() {
    var entidadesBeneficiarias = beneficiariosRepository.getInstancias();

    return donacionesRepository.getDonacionesEnDeposito().stream()
        .map(donacion -> {
          var candidatas = algoritmoDeAsignacion.crearRanking(donacion, entidadesBeneficiarias);
          SolicitudDeAsignacion solicitud = new SolicitudDeAsignacion(donacion, candidatas);
          solicitudesDeAsignacionRepository.registrar(solicitud);
          return solicitud;
        })
        .toList();
  }
```
- Se podía dar el caso de que se creen solicitudes sin entidades candidatas, lo cual no tiene sentido y genera dos problemas:
  - Se pasa la DonaciónIndependiente a `Pendiente_De_Asignacion`
  - Hay que identificar cuáles quedaron sin candidatas para volver hacia atrás la DonacionIndependiente al estado original
- Como que no haya donaciones para ser asignadas es un caso posible y, además, si alguna donación no puede ser asignada no significa que el resto tampoco puedan serlo:
  - **NO** se lanza una excepción
  - Sólo se crea la solicitud de la lista de candidatos tiene al menos 1 elemento

### FiltroDeBusqueda
- Se agregó para no tener que escribir queries a mano que impliquen más de 1 filtro o un operador que no sea "=".
- En un principio se pensó en que Filtro tenga un conjunto de operaciones, lo cual es más flexible; pero hace que haya que preocuparse de combinaciones que no tienen sentido.
- Por una cuestión de tiempo se lo dejó así, pero es **MEJORABLE**

### ¿Notificaciones enviadas en el momento o tarea programada?
- Enviarlas en el momento tiene como ventaja eso: se envian en el momento y no dependes de una tarea programada; pero tiene algunos problemas:
  - ¿cuándo se persisten las notificaciones?
    - Cuando se crean:
      - Te tenes que acordar en **TODOS** los lugares que crees una.
    - Cuando se envían:
      - En el BusDeNotificaciones no es muy cohesivo porque este te debería dar únicamente un medio (bien configurado) dado un TIPO de MEDIO.
      - En MedioDeNotificacion tampoco es muy cohesivo y es problemático porque tenes que determinar si es Nueva para guardarla o vieja y actualizarla.
      - Además, supondría un (INSERT + UPDATE) + la tarea programada
- Persistirlas y no enviarlas al instante:
  - Perdes el envio instantáneo
  - Ganas en cohesión de los componentes
  - **TODOS** saben que para enviar una notificación simplemente la guardas y ya
  - Puede ser mejor para los usuarios porque su request (síncrona) no se ve afectada por el "llamado" al servicio externo de notificaciones y la espera que eso supone.
  - Se puede mejorar la performance agregando un índice a las columnas que se necesitan para determinar si una Notificación es enviable:
```java
@Entity
@Table(
    name = "notificaciones",
    indexes = {
        @Index(name = "idx_notificaciones_pendientes", columnList = "fue_entregada, veces_rebotada")
    }
)
```

### Router donde se hace todo vs Router que recibe una lista de interfaces que saben agregar endpoints
- La primera opción hace que el servidor sea más extensible o que se vea menos afectado frente a nuevas rutas o grupo de rutas, pero hace al Router menos genérico/reutilizable y se puede volver demasiado grande
- La segunda opción hace que el servidor se vea afectado si se agregan nuevos grupos de rutas, pero hace que el Router sea más extensible y reutilizable.
- Realmente

### La tarea de ejecutar la creación de solicitudes de asignación debería devolver o no?
- Si devuelve lo que creo es fácilmente identificable lo que creo, lo cual hace que sea obvio lo que hay que responder a la ruta que ejecuta un algoritmo de asignacion a demanda
- Si no devuelve, ¿cómo identificas lo que se creo?
  - Si haces un getAll by fecha de creación, como hay ejecución a demanda, podría pasar que hayan solicitudes que no fueron creadas por esa request
  - Se podría dar entidad a la corrida que los creo, pero quizá es complicarla demasiado.

## Logistica-Service

### General
- Los constructores vacíos que requiere JPA se crean como `protected` de forma que se explicita que
  están ahí únicamente por ese motivo y no por una necesidad real del dominio.
- Los campos propios no tendrán ningún tipo de prefijo o sufijo, pero los campos que provengan desde
  otra tabla (`fk`) o que estén embebidos sí se llamaran: `nombreDeLaEntidadDeLaQueProvienen_nombreCampo`.


### Camion
- Se creó un constructor vacío porque así lo requiere JPA
- Se usó como ID uno subrogado y no la patente porque, a pesar de que las patentes son únicas y no
  cambian, tienen distintos formatos y,con los años, pueden surgir más sin estar "normalizados".
- Por ahora, se le agregó el getId y el Id, pero no se descarta generar una entidad "Persistible" con
  MappedSuperclass para no estar reptiendo esta lógica en todas las clases que lo requieran.
- Los atributos de camión que antes eran `final` tuvieron que perder esa propiedad por la existencia
  del constructor vacío.
- ¿Qué hace Configuracion en Camion?

### Posicion
- Se optó por embeberla en el Camión
- No interesa la identidad, sino que lo que importa es su estado interno. Dos posiciones son las mis-
  mas si coinciden en longitud y latitud.
- La posición NUNCA se actualiza, ya que si el camión cambia de posición lo que hace es setear una
  nueva.
- Plantear la Posición como una `entidad` supone una complejidad mayor a la hora de persistir,
  ya que habría que chequear que no exista previamente y, en caso de que sí exista, reutilizarla. Además,
  hay casos bordes como, por ejemplo, dos Posiciones que difieren un 0,00000000000001 metros, ¿son la
  misma o no lo son?
- No ganamos nada modelando que `N` camiones están relacionados a la misma Posición. Si se da es algo
  accidental y momentáneo.
- No se persistió la velocidad teniendo en cuenta que estaba deprecada
- Se eliminó la propiedad `final` de los atributos porque se requirió un constructor vacío.
- Se agregó el método esIgual sin pisar el equals/hashcode por simplicidad, quizá tenga sentido hacer-
  lo
- No se agregó el método `estaEnLaMismaPosicionQue(Camion otroCamion)` a Camion porque ese método
  tiene sentido únicamente en el contexto de los tests y se usa una sóla vez.

### Ruta
- Se mantuvo el viejo `id`, que era de tipo String, únicamente para no romper todo. La idea es, even-
  tualmente, eliminarlo y reemplazarlo por `idRuta`.
- Se ignoró la lista de entregas, porque todavía no se persistió la Entrega
- Se ignoró al Camión porque supone una relación Bidireccional innecesaria.

### Bidireccionalidad Ruta-Camion
- Un `Camion` conoce muchas `Rutas` y una `Ruta` conoce 1 sólo `Camión`
- Mantenerla:
  - `no` es necesario
- Convertirla en una relación `unidireccional`:
  - Puede plantearse, desde el lado de Objetos, que un `Camión` tiene una lista de `Rutas` o que una
    `Ruta` conoce el `Camión` que le fue asignado.
  - Los requerimientos del sistema no posicionan a alguna de las dos opciones por sobre la otra.
  - Si se hace con la lista de Rutas en Camion:
    - el estar disponible/ocupado puede ser una propiedad calculada
    - la ruta actual es la del día de la fecha y que está en-curso
    - la interfaz del Camión probablemente tenga los mensajes iniciarRuta/finalizarRuta que no se-
      rían un MissPlaced Method porque el Camión aportaría la lógica de obtener su Ruta actual
    - los mensajes iniciarRuta y finalizarRuta en Ruta recibirían el Camión como dependencia
    - se podría mantener la interfaz de la api planteada en la entrega anterior
    - si llega a surgir un requerimiento en el que tengas que trabajar con todas las rutas de un
      Camion, entonces es más simple porque ya tenes una entidad que las conoce
    - La request de actualizar la posición del Camión es **MUCHO** más frecuente que la de iniciar
      la ruta.
    - Podría haber 2 atributos: `rutasHistóricas` y `rutaActual`, pero, como las rutas conocen su
      estado y la fecha en que deben ser recorridas, entonces es posible identificar a la `rutaActual`
      fácilmente sin romper encapsulamiento. Quizá lo malo de hacerlo así es que no existe el concepto
      de `rutaActual` en el mundo relacional.
  - Si se hace con el Camion en cada Ruta:
    - Camion no tendría en su interfaz mensajes relacionados a las Rutas
    - `estaDisponible` pasaría a ser un atributo no calculado
    - el seteo de `estaDisponible` tendría que recibir como parámetro la Ruta para chequear que
      la que le manda el mensaje sea la Ruta Actual del Camión
    - No tendría mucho sentido la forma en que se planteó la API y habría que ver si los Camione-
      ros conocen la Ruta o qué
- Se va a resolver con listado histórico de rutas en Camión y eliminando la relación de Ruta.

### EstadoRuta como enum vs State
- Son 3 estados y el comportamiento diferenciado es muy poco, además de que con un if (sin anidar)
  o plantear 3 casos distintos alcanza. Por eso, se planteó como enum.

### RutaAsignada Optional vs Null Object
- Realmente la dos son válidas. Entiendo que, quizá, el Optional es más del paradigma Funcional que
  del de Objetos; pero cumple la misma función.
- Lo único es que usarlo hace que en Camión se tenga una mayor conciencia de la posibilidad de que
  pueda estar o no. Mientras que si usáramos el Null Object esa lógica no estaría presente en Camión.
- Es más simple el Optional en cuanto a la cantidad de entidades, aunque quizá es más complejo a nivel
  código.

### Agregar los métodos `iniciarRuta`, `finalizarRuta`, `totalDeEntregasCompletadas`, `totalDeEntregas`, `getIdRutaAsignada`, etc:
- Puede que sea menos cohesivo que antes, pero hay lógica que estaba desparramada en el controller
  volviéndolo más un Service.
- Además, la lógica relacionada a la Ubicación es mínima y no parece que vaya a crecer, así que
  son manejables ambas cosas.

### `estaEn` un determinado estado
- Es una idea que aparece mucho en clases que representan estados como enums y, tranquilamente, se
  podría abstraer una idea común para no repetir código; pero es tan poco que no tiene sentido gastar
  la herencia en eso.

### Pasar Camion en las operaciones iniciar/finalizar ruta
- Es una forma de reflejar la bidireccionalidad en el modelado de objetos, sin explicitarla innecesariamente

### Entrega
- La mayoría de es atributos son básicos, es decir se mapean directamente.
- El viejo entregaId debería desaparecer
- El `estado` puede ser redudante respecto a historialDeEstados
- Hay dos atributos que son "complejos":
  - historialDeEstados
  - entregaPublicador

### EstadoHistorico
- Lo que nos interesa es su estado interno, pero no `EstadoHistórico` como entidad. Con esto me refiero a que no nos interesa representar que dos `Entregas` que tengan estadosHistóricos con los mismos valores apunten al mismo `EstadoHistórico`.
- Si se da lo anteriormente expuesto es casualidad y no significa nada. De hecho, de hacerlo así el proceso de registrar/eliminar un estado histórico se volvería muy tedioso.
- En consecuncia, se determinó que es un @Embebible y **NO** una @Entidad.

### Entrega: historialDeEstados
- Como se determinó que EstadoHistórico es un Embebible, simplemente se mapeo la persistencia de dicho atributo como @ElementCollection + @CollectionTable, que es el combo para cuando se relaciona con un Embebible y la relación es de @OneToMany.
- Es una lista, con lo cual se podría agregar @OrderColumn; pero, realmente, nunca se hace uso del orden. De hecho, `EstadoHistorico` tiene la fecha de transición que, en última instancia, permitiría ordenarlos de ser necesario.
- EstadoHistorico usa LocalDate para la fecha de transición, pero sería más correcto usar LocalDateTime porque, en teoría, todas las transiciones o, al menos, la mayoría van a ocurrir el **MISMO DÍA**.
- Con eso en mente, perdería sentido que sea una lista y podría ser, tranquilamente, un **SET**.
- Se pasó de List -> Set

### Entrega: entregaPublicador
- ¿Tiene sentido persistir el publicador de eventos?
- Siempre seteamos los mismos eventos para todas las entregas, por ende cuando las creamos ya
  podemos inyectar el publicador de eventos con todos los observers suscriptos y lo mismo ocurre cuando
  lo traemos desde la base de datos.
- Si no lo persistís, ¿cómo hace JPA para darte una instancia de Entrega configurada correctamente?
  - Agregar un método en Entrega que tenga la notación @PostLoad:
    - Acá deberías setear el publicador de Entregas, pero cómo conoces al Publicador de Entregas?
    - Necesitas algo conocido globalmente que te de acceso al Publicador de Entregas
    - Es raro porque es un método que sólo sirve para que JPA configure correctamente las instancias
  - Sacar el Publicador de la Entrega y ponerlo en el Repository:
    - Cada método que trae una o varias instancias de Entrega se tiene que "acordar" de setearle el
      publicador
    - Abrís la puerta a que no todas las entregas tengan el mismo publicador y, en consecuencia, los
      mismos observers.
    - El repository pierde cohesión (discutible) o, al menos, está más acoplado a JPA y a una cuestión
      puramente de persistencia.
  - Pasar el Publicador como argumento en cada método de Entrega que modifique su estado (EstadoEntrega):
    - Es molesto porque agrega 1 dependencia extra a cada método de cambio de estado
    - Pierde un poco la gracia porque quien envía el mensaje está siendo completamente consciente de
      que el método puede generar un evento y, más aún, tiene la postestad de decidir qué observers van
      a ser, potencialmente, notificados.
  - Poner el publicador como estado de la clase:
    - Todas las instancias van a conocer al mismo Publicador
    - No se puede suscribir a una Entrega en particular, uno se suscribe a "Todas" las Entregas. Igualmente, esto no era una necesidad real, es decir no se está perdiendo nada en realidad.
    - Necesitas permitir un método que setee el Publicador, con lo cual cualquiera podría modificarlo (aunque se podría agregar validaciones para evitarlo).
    - En los tests, para que sean independientes, hay que setear de nuevo el publicador para "limpiarlos".
    - La otra posibilidad es sólo dar la posibilidad de suscribrir/desuscribir, entonces después de cada test podes desuscribir
    - ¿Cuándo lo seteas?
      - Cuando haces new LogisticaInjector:
        - Acá te aseguras que siempre esté bien configurado, pero te ata a hacer desuscrbirTodos si no lo necesitas
        - Podrías tener un stategy en el Logistica Injector que lo inicialice distinto
        - Podrías tener distintos injectors según el ambiente
  - Podrías persistir los publicadores y los observers:
    - Te da la posibilidad de tener entregas con distintos observers suscriptos y te olvidas del estado global y los problemas que trae aparejado.
  - El gran problema de no persistirlo es que te atas al servidor que se levante o al LogisticaInjector al que se le haya hecho new en el main que se levantó.
### Ruta: Entrega
- Se persistió como un OneToMany porque una Entrega se relaciona con una Ruta y una Ruta se relaciona con muchas Entregas.
- Se agregó el `(cascade = CascadeType.PERSIST)` porque se entiende que las Entregas nacen siempre para ser asociadas a una Ruta y, de esta forma, cuando se crea una Ruta y se la registra, también se está registrando una Entrega.

### RutasRepository: buscarEntregaPorId XXX EntregasRepository
- Es una funcionalidad que antes se encontraba en RutasRepository, pero que claramente es de EntregasRepository

### FiltroGlobalDeTransacciones
- Se puso un MIDDLEWARE para crear transacciones para todas las requests que sean HTTP y no sean GETs, es decir de sólo lectura.
- En el try/catch los errores producto de JAVALIN o de DOMINIO se los relanzan de esa misma forma y los que no sean de ese tipo son wrapeados como errores de Transaccion.
- Quizá es mejor explicitar en las operaciones que se usa porque es más fácil de ver para nosotros y para testear. Si fuera un framework quizá tendría más sentido el middleware.

### GeneradorDeUrlDeTracking | AlmacenamientoDeComprobantes
- Antes estas responsabilidades estaban en el Controller de Entregas, lo cual era poco cohesivo. Por ende, se crearon estas abstracciones para quitarle dichas responsabilidad. Esto también genera beneficios a nivel de testeabilidad.

### Equals para entidades persistibles
- Se deja de usar:
  - Reescribir hashcode/equals
  - Usar un método auxiliar esIgual --> Con este siento que es más fácil no cometer un error.

### ¿Usamos el ID que nos da JPA o usamos una PK natural?
- Para persistir y a nivel base de datos usamos la de JPA, pero para el dominio usamos una PK natural que sabemos que va a estar siempre, aún sin persistencia.
- Desacopla al Dominio de la persistencia y para quien usa/testea se vuelve irrelevante si las entidades ya tienen los ids de JPA o no.
- La estrategia de construcción de esos nombres es un tanto rudimentaria y hasta se podría abstraer porque hay cierta lógica repetida, pero es mínima.

### RepositorioBasico
- Agrupa las operaciones básicas que se repetían entre Repositorios
- Reduce lógica repetida
- Facilitia extensibilidad

### Persistible
- Era una idea que ya había surgido en entregas previas a la `Persistencia`.
- En este caso como todas las entidades tienen la misma estrategia de generacion de id y, además, no hay ningún tipo de relación real entre ellas se aprovechó para no tener que definir en cada caso el id y el getter.


### Entrega: EstadoEntrega
- Se eliminó el atributo ya que se puede calcular a través del historial.

## Donaciones-Service
## Refactor de Repositories para Persistencia (Entrega 3)

### Problema
La clase abstracta `Repository` mezclaba dos responsabilidades: definía el contrato de operaciones (registrar, findById, actualizar, etc.) y la implementación de almacenamiento (una `List` en memoria). Esto impedía cambiar la estrategia de persistencia sin romper todo el código dependiente.

### Solución
Convertimos `Repository` de clase abstracta a interfaz, y creamos dos implementaciones concretas para cada repositorio:
- `RepositoryEnMemoria`: mantiene la lógica original con `List` en memoria
- `RepositoryConPersistencia`: nueva implementación que usará JPA con `EntityManager`

### Justificación
- **Separación de responsabilidades**: la interfaz define *qué* operaciones existen, cada implementación decide *cómo* almacenar los datos
- **Facilita la integración con JPA**: podemos agregar persistencia sin modificar controllers, servicios ni tests existentes
- **Consistencia arquitectónica**: replica el patrón ya usado en `logistica-service` (FlotaRepository, RutasRepository, EntregasRepository)
- **Testeabilidad**: los tests siguen usando `EnMemoria` (rápido, sin BD), mientras que producción usa `ConPersistencia`

### Volvemos al Injector
- Funciona como un ConfigurationRoot que provee instancias bien configuradas para _inyectar_ y **NO** para que no haya DI.
- Crear todo al hacer `new` puede tener como ventaja que sólo se crea 1 vez, pero queda un constructor un tanto inmanejable.

### ServiceLocator es antipatrón?
- oculta la dependencias
- en este caso, se usa para que JPA pueda, a través del @PostLoad o del constructor vacío, `popular` la donación con el publicador que se haya configurado.

### Hay diferencia entre ServiceLocator y EstadoGlobal?
- Ambos habría que setearlos al arrancar
- Ambos pueden ser modificados por cualquiera en cualquier momento
- Ambos resuelven el problema de "persistir" el publicador de cambios de estado con sus observers

### Ventaja ServiceLocator
- Mantiene el publicador propio de cada donación y, si bien no es algo que pase realmente en nuestro sistema, permite que haya donaciones con distintos publicadores (**Repito** no es algo que nuestro sistema requiera ni aproveche)
- La interfaz de DonacionIndependiente evidencia que las donaciones independientes hacen uso de un publicador de eventos

### Desventaja ServiceLocator
- Al ser estado global hay que limpiarlo entre tests (?)
- El repositorio de donaciones tiene un método `recibirDonacion` que recibe una Donción y a raíz de eso registra `N` DonacionesIndependientes. Esta lógica es mucho más difícil teniendo en cuenta que el publicador es propio de cada Entrega porque implica pensar:
  - con qué publicador se deben crear?
  - de dónde se saca el publicador con el que se deben crear?
    - Lo tiene inyectado donacionesRepository?
    - Se lo obtiene desde el ServiceLocator?

### Ventaja Publicador como static y compartido por todas las instancias:
- Es igualmente configurable el publicador
- Las donaciones creadas por el repositorio de donaciones no se tiene que preocupar por qué publicador usar para crearlas

### Desvenataja Publicador como static y compartido por todas las instancias:
- La **NO** interfaz de DonacionIndependiente evidencia que las donaciones independientes hacen uso de un publicador de eventos
- Si algun test setea el estado global hay que limpiarlo después de hacerlo para no afectar al resto
- **No** permite crear donaciones, en los tests, con distintos publicadores --> No deseado
- La interfaz DonacionIndependiente queda sucia porque pasa a tener la responsabilidad del publicador y de ser el punto de acceso para manejar el publicador global de todas las donaciones

## Correciones Post Entrega 3

### Lote con donaciones (I) o donaciones con LoteTest (II)
- Realmente la alternativa que modela mejor lo que se necesita es Donaciones con LoteTest porque todas las operaciones de la donación no pasan por el lote ni lo necesitan.
- La alternativa (I) hace que sea sencillo determinar qué ruta es la que se necesita para crear donaciones: **POST** /lotes con el siguiente body:
```json
{
  "id_donante": 1,
  "bienes": []
}
```
- La alternativa (I) creo que facilita el hecho de que no se puedan crear más de una donación de un mismo tipo de bien asociada a un mismo lote
- La alternativa (I) tiene como redundancia al donante tanto en Donacion como en LoteTest
- Ambas alternativas reflejan la relación a nivel persistencia
- La alternativa (II) **NO** tiene la redundancia del donante
- La alternativa (II) **NO** genera la colección siendo que hoy es innecesaria
- La alternativa (II) dificulta más el proceso de crear o, al menos, de definir una ruta REST para la creación de los recursos, ya que el LOTE y las donaciones se crean al mismo tiempo
- Realmente, no mueve mucho la aguja una o la otra y como una simplifica el proceso de creado -> se elige esa.

### Relación Lote con DonacionIndependiente
- Un LoteTest conoce muchas donaciones independientes y cada donación independiente se relaciona con un LoteTest: One to Many
- Se debe agregar la anotación @JoinColumn

### Categoria: cambio semántico
- Las Categorias dejan a referenciar a Perecedero/NoPerecedero y pasan a hacer referencia a los valores que agrupan a las SubCategorías y tienen una Unidad

### Categoria: Entidad
- Las Categorías pasan a ser Entidades de forma que tienen una tabla **PROPIA** en la base de datos.

### Unidad: enums
- Son valores acotados, no interesa tener una super libertad para crear y tampoco se considera algo común que haya que crear nuevas, entonces se las modela como enums.

### SubCategoria: Entidad
- Las SubCategoria pasan a ser Entidades de forma que tienen una tabla **PROPIA** en la base de datos.
- Cuando en el front se quiera donar cosas o se quieran crear necesidades se va a usar a las SubCategorías para representar la idea general de los Bienes.
- Una forma alternativa quizas sería: que no exista y que los Bienes tengan una Categoría y un Nombre (quizás más simple)

### Nombre de la SubCategoria/Categoria: String
- Se pierde la normalización de datos dada por los Strings para ganar flexibilidad a la hora de crear nuevas SubCategoria/Categoria:
  - Si se usan enums, para crear nuevas, hay que deployar porque hay que tocar el código.
  - Se pueden crear fácilmente sin necesidad de un deploy
- El nombre debe ser UNIQUE
- Esta opción es hasta mejor si se piensa en integración con otros sistemas porque no necesitas avisarles si se crea una nueva para que la soporten (!)

### SubCategoria: esPercedera
- Si bien este booleano es sólo aceptable cuando hay tanto sólo dos tipos Bien, la realidad es que no se planea crear nuevos tipos; entonces alcanza.
- Es cierto que acopla la SubCategoria a los BIENES, pero esas dos ideas ya están acopladas y, de esta forma, se pueden hacer validaciones o mostrar cosas adecuadas desde el front.

### Bien: Entidad
- Los bienes pasan a ser entidades y a representar cada bien físico y no la idea "general" de un bien.
- Puede que a nivel almacenamiento esta interpretación no sea la más conveniente, pero simplifica bastante la persistencia/eliminación de los bienes.
- En cuanto a la trazabilidad permite el trackeo exacto de cada bien y en las donaciones es mucho más claro de qué forma están conformadas, valga la redundancia.

### Bien: Percedero y NoPerecedero
- Se abandona la interfaz y pasan a ser subtipos de Bien.

### Necesidades
- Conocen una CondicionDeAceptacion
- Una Cantidad

### CondicionDeAceptacion
- Se plantea como Entidad porque hay que hacer mapeo de herencia y no son stateless (descarta el uso de enum)

### Item
- Permite representar el formato factura

### Item es embebible o es una entidad?
- La realidad es que **NO** interesa una tabla de ITEMS
- Lo que importa es la Donacion con qué Bien se relaciona y con cuántas unidades de dicho Bien
- Como ganaron las fotos, entonces sí tienen identidad

### Item-Bien: OneToOne o ManyToOne?
- Depende:
  - Si se limita la creación de BIENES y se chequea que no exista una BIEN con las características del Bien que se quiere insertar, entonces siempre se usa el mismo Bien:
    - ManyToOne
  - Si se crea/inserta siempre un nuevo Bien, entonces OneToOne
- La primera alternativa cuida el almacenamiento, pero hace que las operacion de insertar sea más tediosa
- La segunda no cuida el almacenamiento, pero insertar se vuelve mucho más fácil y eliminar también

### Lote: crearLote en el Controller
- Se mantiene un DTO que espera recibir ItemDto (BienDto, contenido y Fotos) por YAGNI
- Seguramente, en el futuro cuando haya un formulario, se tenga que repensar cómo resolver la cuestión de la lista de items y qué hacer con las fotos.
- Una opción es manejar una noción de Borrador en sesión o en objetos

### Necesidades Controller
- En un futuro se debería eliminar de sus rutas el /entidades-beneficiarias/:id y dejar sólo /necesidades asumiendo sesión

## Entrega 4

### Direccion
- Antes era un String, lo cual no era muy útil para validar ni para mostrar en un mapa
- Se cambió por algo que tenga una calle y una posición, o sea coordenadas
- Es embebible

### Calle
- Simplemente algo que agrupa nombre de calle y número
- Es embebible

### Posicion
- Algo con Latitud y Longitud
- Claramente es embebible

### Usuario
- Como el Login es **unificado**, entonces estas obligado a que haya algo que trate polimórifcamente a los donantes, administradores y a las entidades beneficiarias
- Teniendo lo anterior en cuenta hay 4 posibilidades:
  - Una clase Usuario que conozca a un donante, una entidad beneficiaria o un admin
  - Una clase Usuario de la que hereden donante, entidad beneficiaria y admin
  - Una clase Usuario de la que hereden 3 subtipos y que cada subtipo conozca a un donante, una entidad benefiaria y un admin respectivamente.
  - Una clase Usuario que conoce Strategias de mostrar información (es más extensible en relación a Usuario), pero es más compleja la configuración
- La primera opción es muy parecida a la segunda en cuanto a la Simplicidad, pero no gasta la herencia; sin embargo, para ser plausible Donante, EntidadBeneficiaria y Administrador deberían heredar de la misma clase para poder ser tratados polimórficamente por una entidad que es persistida, con lo cual la herencia la terminarías gastando.
- La tercera opción es más compleja (más cantidad de entidades), menos extensible; pero, a nivel persistencia, es mucho más simple porque no necesitas pensar en una clase que englobe a Donante, EntidadBeneficaria y Administrador (que no tienen nada en común). Además, te permite registrar donantes sin que tengan una cuenta (trazabilidad) y, luego, podes crearle la cuenta y asociarla al donante pre-existente (página especial + validación vía sms, wpp o mail)

## Puesta a punto

### Mapa
- Se cambiaron las herramientas utilizadas para que diga "Islas malvinas" y no "Falkland islands".
- Se modificaron los íconos porque antes representaban ubicaciones y, en realidad, lo que se busca mostrar son las donaciones entregadas


### Mis-Donaciones
- Se eliminó la columna `donante` porque era redundante

### Bienvenido/a en el dashboard
- Es propenso a que alguien no se sienta incluido, entonces se lo modificó por **Hola**
