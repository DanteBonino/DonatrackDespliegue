# GitFlow TP Anual

### 1. Elegir una _feature_
### 2. Crear una rama:
    git checkout -b nombre_feature
### 3. Desarrollar _feature_
### 4. Armar un documento de deciciones de diseño
    No tiene que describir el código, tiene que explicar qué alternativas se les ocurrieron para resolver
    la _feature_ y, en base a las cualidades de diseño, el motivo por el cual eligieron la que quedó.
### 4. Crear **tests** (!)
### 5. Correr TODOS los tests ya existentes y que pasen
### 6. Crear Pull Request
### 7. Avisar que está el PR al grupo de WPP
### 8. Lo revisamos
### 9. Lo mergeamos
### 10. Eliminar la branch (!)
```bash
git branch -d nombre-rama                    # borra local
git push origin --delete nombre-rama         # borra remota
```
Se puede configurar github para que la elimine directamente con el PR