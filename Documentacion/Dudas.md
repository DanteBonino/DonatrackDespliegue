# Dudas:
- El sistema de factura implica que se agrupa por mismo "gramaje";
- pero el enunciado habla de diferenciar según Tipo de Bien y dentro eso por Perecedero y No Perecedero según fecha de vencimiento.
- Entonces, si usas el sistema de factura no tenes forma de diferenciar gramajes distintos.
```java
  class LoteTest {
  Donante donante;
  List<Donacion> donaciones;
  }

/*
* El estilo Factura asume todos los bienes homogéneos en:
* - cantidad (o al menos no lo podes determinar en función de la Donación) -> No te interesa diferenciar porque la entidad que los reciba va a recibir la cantidad independientemente de cuántos paquetes representen.
* - estado de uso o fecha de vencimiento (aunque en este punto todavía no aparecieron)
* Trata al Bien como la idea general (la esencia) -> el Bien, hasta acá, podría no conocer su Cantidad y sería un enum
* */

class DonacionConFactura {
int cantidad = 500;
Bien bien;
}

/*
* El estilo sin Factura no asume nada de los bienes, sólo los agrupa (hasta acá) por SubCategoria.
* Cada instancia de Bien representa un Bien en particular
* */
  class DonacionSinFactura {//
  List<Bien> bienes; //Estos son bienes del mismo tipo, pero pueden tener distinto gramaje

public int cantidad() {
return bienes.sum(bien -> bien.cantidad());
}
}

//Esto es Bien para DonacionSinFactura

class Bien {
int cantidad;
Subcategoria subcategoria;
}

enum Subcategoria {
FIDEOSSECOS(ALIMENTO, KG), ARROZ(ALIMENTO, KG), REMERA(VESITMENTA, UNIDAD)
}

enum Categoria {
ALIMENTO, VESTIMENTA
}

//Una forma alternativa podría ser que asumamos que cada Categoria tiene una única unidad asociada y ya.
enum Subcategoria {
FIDEOSSECOS(ALIMENTO), ARROZ(ALIMENTO), REMERA(VESITMENTA)
}

enum CategoriaConUnidad {
ALIMENTO(KG), VESTIMENTA(UNIDAD)
}

enum Unidad {
KG, UNIDAD
}

/*
¿Qué requieren las Necesidades?
- Si estas en el caso de "con factura", entonces un Bien(enum) + cantidad + condición (esto tiene más sentido cuando se agregue el concepto de Perecedero/NoPerecedero)
- Si estas en el caso "sin factura": podría requerir un Bien (ya tiene la idea de Subcategoria + cantidad) + condición: aunque es confuso porque lo que requiere no es estrictamente un Bien particular, sino N cantidad de Bienes que cumplan con la condición   
  */
````