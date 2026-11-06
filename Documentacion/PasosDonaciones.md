# Pasos donaciones

## Paso 1 - crear donante jurídico
```json
{
  "tipoPersona": "HUMANA",
  "documento": "45459090",
  "nombre": "Dante",
  "apellido": "Perez",
  "fechaDeNacimiento": "2004-10-26",
  "genero": "MASCULINO",
  "direccion": "UTN FRBA MEDRANO",
  "email": "dante@example.com",
  "telefono": "1120304050"
}

```

## Paso 2 - crear entidad beneficiaria con necesidad activa
```json
{
  "tipoPersona": "HUMANA",
  "documento": "45459090",
  "nombre": "Dante",
  "apellido": "Perez",
  "fechaDeNacimiento": "2004-10-26",
  "genero": "MASCULINO",
  "direccion": "UTN FRBA MEDRANO",
  "email": "dante@example.com",
  "telefono": "1120304050"
}

```
## Paso 3 - crear donacion con el id del donante juridico
```json
{
  "idDonante": "{{donanteJuridico}}",
  "bienes": [
    {
      "descripcion": "Paquete de fideos secos 500g",
      "foto": "https://example.com/fideos.jpg",
      "cantidad": 10,
      "subCategoria": {
        "nombre": "FIDEOSSECOS",
        "categoria": { "tipo": "PERECEDERO", "fechaDeVencimiento": "2027-03-15" }
      }
    }
  ]
}

```
## Paso 4 - ejecutar el algoritmo de asignación

## Paso 5 - aceptar solicitud pendiente con el id de la entidad beneficiaria creada
```json
{
  "idEntidadBeneficiaria": "{idEntidadBeneficiaria}"
}

```
## Paso 6 - volver a mandar la misma request para mostrar que no se puede volver a aceptar



