# Configuración VM de Azure

## Grupo de recursos
- Carpeta lógica donde Azure agrupa todo lo de la VM
- Al terminar lo borrás y desaparece todo lo que cobra

## Nombre de la máquina virtual
- Identificador interno dentro de Azure
  - **NO** determina el nombre del sitio al que los usuarios acceden para ver la página web

## Tamaño
- Standard_D2as_v6
- 2 vcpus
- 8 GiB ram
- us$92,71 por mes 

## Puertos abiertos
- 80 (HTTP)
- 8080 (HTTP)
- 443 (HTTPS)
- 22 (SSH)

## Chequeo de IP
- SKU:
  - Estandar
  - Estática

## Seteo de DNS
- donatrack-grupo1.brazilsouth.cloudapp.azure.com

## Configuración zona horaria
- Esto es para cuando se configure el crontab con las tareas programadas porque sino va a funcionar de forma inesperada
- sudo timedatectl set-timezone America/Argentina/Buenos_Aires
- Para chequearlo se usa: timedatectl