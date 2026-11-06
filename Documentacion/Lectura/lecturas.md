# Literatura complementaria
- withers
- builder como static class en la clase de dominio

# Investigación
- ## Reflection / Class.forName en Java Términos para buscar:
    - "Java reflection Class.forName tutorial" 
    - "Java reflection design pattern factory" 
    - "reflective instantiation Java pros and cons" 
    - "Class.forName vs reflection factory pattern"
- ### Dónde mirar:
  - La documentación oficial de Oracle sobre Reflection — es el punto de partida más confiable, cubre Class.forName, getDeclaredConstructor, newInstance. Buscá específicamente el capítulo de "Reflection" en el libro Effective Java de Joshua Bloch (Item sobre reflection) — ahí discute exactamente el tradeoff que hablamos: pierde chequeo en compile-time, es más lento, más difícil de debuggear. Para el lado de "cuándo conviene": buscá "reflection based plugin architecture Java" — es el caso de uso típico donde este patrón brilla (plugins que se cargan sin que el core los conozca).

- ## Interpolación de mensajes vs. concatenación Términos para buscar:
    - "Java MessageFormat tutorial"
    - "String template pattern Java" (o buscá directamente "text blocks Java" si querés ver la evolución hacia algo más moderno) "separate message template from data pattern" 
    - Si querés el ángulo de diseño: "internationalization i18n message pattern" — la razón histórica de por qué se separa plantilla de datos es justamente i18n, y ahí vas a encontrar el fundamento completo.
- ### Dónde mirar:
    - Documentación de java.text.MessageFormat en la API de Java — simple y va a la práctica. Si tu cátedra tocó algo de patrones de diseño para notificaciones, buscá si mencionan Template Method aplicado a mensajes (no es exactamente lo mismo pero está emparentado conceptualmente).