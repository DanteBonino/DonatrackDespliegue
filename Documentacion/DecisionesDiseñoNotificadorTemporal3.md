# Primera Entrega

## Notificador como interfaz (Deprecado)
    - Se decidió implementar la idea de Notificador como una interfaz común para los medios de comunicacion.
    - Si bien todavía no hay información suficiente, dado lo que dice el enunciado se presume que requieren
      servicios externos distintos. Por ende, la idea a dónde le tiene que pegar y qué formato tiene la info
      que debe enviar. De esta forma se tratan polimórficamente y se independiza de a qué medio se le está pidiendo
      que notifique. Por ende, es fácilmente extensible.
    - Todavía no hay información concluyente de si se precisa tener trazabilidad de las notificaciones.
      En cuyo caso quizá se justifica tener un componente Notificador y cambiar un poco la implementación.
    - Hoy por hoy podría haber una única clase Notificador, ya que como la información respecto a los servicios
      externos es tan poca que todas hacen lo mismo. Quedó la interfaz más por saber que el diseño acá es pobre
      dada la falta de conocimiento.
    - Lo negativo de no tener un Componente Notificador es que de pronto hay componentes que van a tener
      la misma lógica o conocimiento de saber cómo se mandan notificaciones y qué notificaciones crear. Por este
      motivo en un futuro quizá tenga sentido tener un componente que sea responsable de esto.
    - Se descartó la idea de interfaz compartida y 3 clases vacías, porque sería lo mismo que tener un enum
      con 3 valores posibles. Es más simple e igualmente extensible (la clave es que hay muy poca info y que todavía
      no requieren estado mutable.

## Estado Notificacion (Deprecado)
    - Cada Notificacion puede tener 1 de 3 estados, NO LEIDO, LEIDO, COMPLETADO, cuando se manda
      un mensaje por default sale como NO_LEIDO.

## Notificacion (Deprecado)
    - El mensaje a enviar.

# Segunda Entrega
    - En la primera entrega se estabamos dudando de si la interfaz Notificador se justificaba, dado que con notificaciones simuladas
      todos los medios terminaban haciendo lo mismo (por eso surgió lo del enum). En esta entrega se confirmó que la implementación de una 
      interfaz era correcta, cada medio tiene una implementación diferente vía proveedor externo. Por lo que usar un Enum violaría el principio
      Open/Close,
    - Se hizo una abstracción sobre Donantes y Entidades Beneficiarias que ahora implementan una interfaz llamado
      Destinatario. Donde el notificador depende solamente de la interfaz destinatario, no de las clases concretas de los actores, 
      logrando un menor acoplamiento y mayor extensibilidad, en caso de querer agregar un nuevo actor al dominio.
    - Se implemento el patrón Observer para el repositorio de Donantes, ahora el Donante desconoce del notificador y de la 
      notificacion a la hora de mandar un aviso al donante cuando dicha persona se registra. Logrando una mayor cohesión y menor
      acoplamiento respentado los principios open close, single responsability, dependency inversion (el repo depende del observer, los 
      notificadores dependen del Destinatario).
    - Los enviós de mensaje ahora lo maneja un OrquestadorNotificaciones y no el repositorio en sí.





