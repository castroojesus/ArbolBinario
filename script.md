# SCRIPT DE EJECUCIÓN: DEMO DEL SISTEMA DE GESTIÓN DE ARCHIVOS

1. Configuración del entorno

   1.1. Compilación de las clases
Abrir la terminal en la carpeta raíz del proyecto (ProyectoArboles) y compilar todos los archivos .java con el comando:  

             javac *.java

   1.2. Ejecución de la demo
En la terminal, ingresar el comando para iniciar el programa:

             java Arbol   
           
3. Script de demostración
El siguiente script demuestra la implementación de las estructuras y operaciones principales requeridas (mkdir, touch, rm, mv, rename, search, export json, import json, help, test, exit).  
   
## Paso 1: configuración inicial (creación)
  Se inicia el árbol en / y se crean las carpetas y archivos iniciales.  
  1. Comando: `mkdir`
* carpeta padre: /
*  nombre nueva carpeta: Documentos
*   "Creado exitsamente"
  
  2. Comando:  `touch`         
* carpeta padre: Documentos
*  nombre del archivo: lista.txt
*  Contenido (opcional): Estructura de datos
*  Creado exitosamente
  
 
## Paso 2: pruebas de consistencia del árbol
Se valida la estructura después de las operaciones de creación (Ej. 4 carpetas + 2 archivos = 6 nodos).
* Comando: `info`
*  Resultado:
*  Tamaño: 5 nodos
*  Altura: 2 niveles

## Paso 3: manipulación y búsqueda (mv, rename, search)
1. Comando: `mv` 
* Elemento a mover: lista.txt
*  Carpeta destino: Datos
*   Movido: lista.txt -> Datos

2. Comando: `rename`
* Nombre actual:  Datos
* Nuevo nombre: DatosArbol
*  Renombrado: Datos -> DatosArbol

3. Comando: `search`
* Prefijo a buscar: Docs

* Autocompletado para 'Docs':
     *  Docs [ID: ffa6...]

   
## Paso 4: persistencia (export e import)   
Se guarda el estado del árbol y se restaura.

1. Comando: `export`
* Nombre del archivo: lista.txt
*  Árbol exportado a: lista.txt
*   ¿Exportar papelera también? (s/n): n

2. Comando: `rm`
* Nombre del elemento a eliminar: lista.txt
*  Eliminado: lista.txt (movido a papelera)

3.Comando: `tree`
ESTRUCTURA DEL ÁRBOL  
═══════════════════════════════  
[DIR] /  
   [DIR] DatosArbol  
     [DIR] Docs  
═══════════════════════════════


## Paso 5: ayuda, tests y salida
Se ejecutan los comandos auxiliares y se finaliza el programa.

1. Comando: `help`

AYUDA  
══════════════════════════════════════════════════════  
Este programa implementa un árbol binario de búsqueda.  
═══════════════════════════════════════════════════════  
Los valores menores van a la izquierda.  
Los valores mayores van a la derecha.

### COMANDOS PRINCIPALES:
* `mkdir` - Crea una nueva carpeta
* `touch` - Crea un nuevo archivo
* `rm` - Elimina un elemento (va a papelera)
* `mv` - Mueve un elemento a otra carpeta
* `rename` - Cambia el nombre de un elemento
* `search` - Busca elementos con autocompletado

### VISUALIZACIÓN:
* `tree` - Muestra la estructura completa
* `info` - Estadísticas del árbol
* `trash` - Ver/vaciar papelera de reciclaje

### PERSISTENCIA:
* `export` - Guarda el árbol en formato JSON
* `import` - Carga un árbol desde JSON

### CARACTERÍSTICAS:
* Autocompletado con Trie
* Papelera de reciclaje
* Tests de integración incluidos
═══════════════════════════════════════════════════════


2. Comando: `test`
===================================================
=                      RESUMEN                      =
===================================================
Tests pasados: 29
Tests fallados: 0

3.Comando: `exit`

BYE...


