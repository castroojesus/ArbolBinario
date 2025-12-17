# ArbolBinario

[![Java](https://img.shields.io/badge/Java-17+-orange)](https://www.oracle.com/java/)
[![Gson](https://img.shields.io/badge/JSON-Gson-green)](https://github.com/google/gson)

Este proyecto simula un cmd de windows, donde puedes usar los comandos de la terminal para crear carpetas y archivos, renombrar, mover, eliminar y exportar dichas carpetas y archivos con la finalidad de entender mas a fondo como funcionan los arboles binarios de busqueda

##  Características

* Este programa lo que hace es:
  * Gestionar archivos:
    * Crear carpetas/archivos
    * Eliminar carpetas/archivos
    * Mover carpetas/archivos
    * Renombrar
    * Exportar el orden jerarquico en un .json
    * Importar desde el .json
    * Busqueda con autocompletado
* Cuenta con un menu interactivo e intuitivo facil de entender

##  Requisitos

* Java JDK 11 o superior
* Eclipse IDE o VS Code
* Libreria GSON 2.10.1

##  Instalación

1. Clonar el repositorio
   ```cmd
   git clone https://github.com/castroojesus/ArbolBinario/tree/main
   ```
2. Abrir tu IDE Eclipse
3. Compilación de las clases Abrir la terminal en la carpeta raíz del proyecto (ProyectoArboles o donde hayas guardado los archivos) y compilar todos los archivos .java con el comando:
   ```
   javac *.java
   ```
4. Compilar el proyecto con el siguiente comando:
   ```
   java Arbol
   ```


## ▶️ Cómo Usarlo

1. Ejecuta el programa.
2. Escribe los comandos que quieras utilizar
* Comando `mkdir`
  * Carpeta padre: `/`
  * Nueva carpeta: `Documentos`
  * "Creado exitosamente"
    
* Comando `touch`
  * Carpeta padre: `Documentos`
  * Nuevo archivo: `Resumen.txt`
  * Contenido (opcional): "Ejemplo de txt"
    
* Comando: `info`
   * Resultado:
   * Tamaño: Nodos despues de creacion o eliminacion
   * Altura: Niveles despues de creacion o eliminacion
     
* Comando: `mv` 
  * Elemento a mover: lista.txt
  * Carpeta destino: Datos
  * Movido: lista.txt -> Datos
 
* Comando: `rename`
  * Nombre actual:  Datos
  * Nuevo nombre: DatosArbol
  * Renombrado: Datos -> DatosArbol

* Comando: `search`
  * Prefijo a buscar: Docs

  * Autocompletado para 'Docs':
     *  Docs [ID: ffa6...]
   
* Comando: `export`
  * Nombre del archivo: lista.txt
  * Árbol exportado a: lista.txt
  * ¿Exportar papelera también? (s/n): n

* Comando: `rm`
  * Nombre del elemento a eliminar: lista.txt
  * Eliminado: lista.txt (movido a papelera)

* Comando: `tree`
  * ESTRUCTURA DEL ÁRBOL  
  * ═══════════════════════════════  
  * [DIR] /  
     * [DIR] DatosArbol  
       * [DIR] Docs  
  * ═══════════════════════════════
 
* Comando: `help`

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

* Comando: `test`
   
RESUMEN:   

Tests pasados: 29
Tests fallados: 0

* Comando: `exit`

BYE...

## 🧩 Estructura del Proyecto

* /src — código fuente
* /docs — documentación
* README.md — este archivo

## ✨ Autor

* Tu nombre




