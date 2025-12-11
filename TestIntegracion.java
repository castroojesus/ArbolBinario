package ProyectoArboles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestIntegracion {

    private static int testsPasados = 0;
    private static int testsFallados = 0;


    private static void assertEq(String nombre, Object esperado, Object actual) {
        if (esperado.equals(actual)) {
            System.out.println( nombre + " PASÓ");
            testsPasados++;
        } else {
            System.out.println( nombre + " FALLÓ");
            System.out.println("   Esperado: " + esperado + " | Actual: " + actual);
            testsFallados++;
        }
    }

    private static void assertTrue(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println( nombre + " PASÓ");
            testsPasados++;
        } else {
            System.out.println( nombre + " FALLÓ");
            testsFallados++;
        }
    }

    private static void assertFalse(String nombre, boolean condicion) {
        assertTrue(nombre, !condicion);
    }

    // ==================== TESTS DE INTEGRACIÓN ====================

    public static void testCicloCompletoCRUD() {
        System.out.println("\n-----------");
        System.out.println("TEST 1");
        System.out.println("-----------");

        Arbol arbol = new Arbol("Root");

        // crear
        arbol.addChildren("Root", "Folder1", "carpeta", null);
        arbol.addChildren("Folder1", "file1.txt", "archivo", "Contenido 1");
        assertEq("Tamaño después de crear", 3, arbol.getSize());

        // leer
        Node nodo = arbol.findNodeByName("file1.txt", arbol.root);
        assertTrue("Nodo encontrado", nodo != null);
        assertEq("Contenido correcto", "Contenido 1", nodo.getContenido());

        //rename
        assertTrue("Renombrar exitoso", arbol.renameNode("file1.txt", "archivo1.txt"));
        nodo = arbol.findNodeByName("archivo1.txt", arbol.root);
        assertTrue("Nodo renombrado encontrado", nodo != null);

        // delete
        assertTrue("Eliminación exitosa", arbol.removeNode("archivo1.txt"));
        assertEq("Tamaño después de eliminar", 2, arbol.getSize());

        System.out.println("--------------------------------------");
    }

    public static void testOperacionesComplejas() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 2: OPERACIONES COMPLEJAS");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("Sistema");

        // Crear estructura compleja
        arbol.addChildren("Sistema", "Usuarios", "carpeta", null);
        arbol.addChildren("Sistema", "Config", "carpeta", null);
        arbol.addChildren("Usuarios", "Admin", "carpeta", null);
        arbol.addChildren("Usuarios", "Guest", "carpeta", null);
        arbol.addChildren("Admin", "perfil.json", "archivo", "{}");
        arbol.addChildren("Config", "settings.ini", "archivo", "config=true");

        assertEq("Tamaño estructura compleja", 7, arbol.getSize());
        assertEq("Altura estructura compleja", 3, arbol.getHeight());

        // Mover archivo entre carpetas
        assertTrue("Mover archivo", arbol.moveNode("perfil.json", "Guest"));
        Node movido = arbol.findNodeByName("perfil.json", arbol.root);
        assertEq("Archivo en nueva ubicación", "Guest", movido.getParent().getNombre());

        // Renombrar carpeta
        arbol.renameNode("Admin", "Administrador");
        assertTrue("Carpeta renombrada", arbol.findNodeByName("Administrador", arbol.root) != null);

        System.out.println("--------------------------------------");
    }

    // ==================== TESTS DE CASOS LÍMITE ====================

    public static void testCasosLimite() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 3: CASOS LÍMITE");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("Root");

        //  eliminar raíz
        assertFalse("No se puede eliminar raíz", arbol.removeNode("Root"));

        //  agregar hijo a un archivo
        arbol.addChildren("Root", "archivo.txt", "archivo", "datos");
        arbol.addChildren("archivo.txt", "hijo.txt", "archivo", "no debería agregarse");
        assertEq("No agregar hijo a archivo", 2, arbol.getSize());

        // buscar nodo inexistente
        assertTrue("Nodo inexistente es null", arbol.findNodeByName("NoExiste", arbol.root) == null);

        // renombrar con nombre vacio
        assertFalse("No renombrar con vacío", arbol.renameNode("archivo.txt", ""));

        // mover a destino invalido
        arbol.addChildren("Root", "carpeta1", "carpeta", null);
        assertFalse("No mover a archivo", arbol.moveNode("carpeta1", "archivo.txt"));

        // arbol con un solo nodo
        Arbol arbolVacio = new Arbol("Solo");
        assertEq("Altura árbol solo raíz", 0, arbolVacio.getHeight());
        assertEq("Tamaño árbol solo raíz", 1, arbolVacio.getSize());

        // nombres duplicados
        arbol.addChildren("Root", "dup.txt", "archivo", "A");
        arbol.addChildren("carpeta1", "dup.txt", "archivo", "B");
        assertEq("Nombres duplicados en diferentes carpetas", 5, arbol.getSize());

        System.out.println("--------------------------------------");
    }

    public static void testArbolProfundo() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 4: ÁRBOL MUY GRANDE");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("Nivel0");

        String padre = "Nivel0";
        int profundidad = 100;

        for (int i = 1; i <= profundidad; i++) {
            String hijo = "Nivel" + i;
            arbol.addChildren(padre, hijo, "carpeta", null);
            padre = hijo;
        }
        assertEq("Tamaño árbol profundo", profundidad + 1, arbol.getSize());
        assertEq("Altura árbol profundo", profundidad, arbol.getHeight());

        Node nodo = arbol.findNodeByName("Nivel50", arbol.root);
        assertTrue("Nodo en profundidad encontrado", nodo != null);


        System.out.println("--------------------------------------");
    }

    public static void testArbolAncho() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 5: ÁRBOL MUY ANCHO");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("Root");

        int anchura = 1000;

        for (int i = 0; i < anchura; i++) {
            arbol.addChildren("Root", "Hijo" + i, "archivo", "Contenido " + i);
        }
        assertEq("Tamaño árbol ancho", anchura + 1, arbol.getSize());
        assertEq("Altura árbol ancho", 1, arbol.getHeight());

        Node nodo = arbol.findNodeByName("Hijo500", arbol.root);
        assertTrue("Nodo en anchura encontrado", nodo != null);


        System.out.println("--------------------------------------");
    }

    // ==================== TESTS DE PERFORMANCES ====================

    public static void testPerformanceGrande() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 6: PERFORMANCE ÁRBOL GRANDE");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("FileSystem");
        Random rand = new Random(42); 

        List<String> carpetas = new ArrayList<>();
        carpetas.add("FileSystem");

        int totalNodos = 5000;
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < totalNodos; i++) {
            String padre = carpetas.get(rand.nextInt(carpetas.size()));

            if (rand.nextDouble() < 0.3) { 
                String nuevaCarpeta = "Folder_" + i;
                arbol.addChildren(padre, nuevaCarpeta, "carpeta", null);
                carpetas.add(nuevaCarpeta);
            } else { 
                arbol.addChildren(padre, "file_" + i + ".txt", "archivo", "Data " + i);
            }
        }

        long fin = System.currentTimeMillis();
        System.out.println("Estadísticas del árbol grande:");
        System.out.println("Nodos totales: " + arbol.getSize());
        System.out.println("Altura: " + arbol.getHeight());
        System.out.println("Carpetas creadas: " + carpetas.size());

        inicio = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            arbol.findNodeByName("file_" + (rand.nextInt(totalNodos)) + ".txt", arbol.root);
        }
        fin = System.currentTimeMillis();
        System.out.println("100 búsquedas: " + (fin - inicio) + "ms");

        System.out.println("--------------------------------------");
    }


    public static void testPersistenciaGrande() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 8: PERSISTENCIA JSON ÁRBOL GRANDE");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("BigTree");

        for (int i = 0; i < 10; i++) {
            arbol.addChildren("BigTree", "Folder" + i, "carpeta", null);
            for (int j = 0; j < 50; j++) {
                arbol.addChildren("Folder" + i, "file_" + i + "_" + j + ".txt", 
                                "archivo", "Datos del archivo " + i + "-" + j);
            }
        }

        int tamañoOriginal = arbol.getSize();
        int alturaOriginal = arbol.getHeight();

        long inicio = System.currentTimeMillis();
        arbol.exportToJSON("test_grande.json");
        long fin = System.currentTimeMillis();
        System.out.println("Exportar " + tamañoOriginal + " nodos: " + (fin - inicio) + "ms");

        Arbol arbolImportado = new Arbol("Temporal");
        inicio = System.currentTimeMillis();
        arbolImportado.importFromJSON("test_grande.json");
        fin = System.currentTimeMillis();
        System.out.println("Importar " + tamañoOriginal + " nodos: " + (fin - inicio) + "ms");

        assertEq("Tamaño tras importar", tamañoOriginal, arbolImportado.getSize());
        assertEq("Altura tras importar", alturaOriginal, arbolImportado.getHeight());

        System.out.println("--------------------------------------");
    }

    public static void testPapeleraCompleja() {
        System.out.println("\n--------------------------------------------");
        System.out.println("TEST 9: PAPELERA CON OPERACIONES COMPLEJAS");
        System.out.println("--------------------------------------------");

        Arbol arbol = new Arbol("Root");

        arbol.addChildren("Root", "Temp1", "carpeta", null);
        arbol.addChildren("Temp1", "file1.txt", "archivo", "A");
        arbol.addChildren("Temp1", "file2.txt", "archivo", "B");
        arbol.addChildren("Root", "Temp2", "carpeta", null);

        int tamañoAntes = arbol.getSize();
        arbol.removeNode("Temp1"); 

        assertEq("Tamaño tras eliminar carpeta", tamañoAntes - 3, arbol.getSize());

        arbol.exportTrashCanToJSON("test_papelera.json");

        arbol.CleanTrashCan();

        System.out.println("--------------------------------------");
    }

    public static void testTrieAutocompletado() {
        System.out.println("\n---------------------------------------");
        System.out.println("TEST 10: TRIE Y AUTOCOMPLETADO");
        System.out.println("---------------------------------------");

        Arbol arbol = new Arbol("Root");

        // Crear archivos con prefijos similares
        arbol.addChildren("Root", "documento1.txt", "archivo", "");
        arbol.addChildren("Root", "documento2.txt", "archivo", "");
        arbol.addChildren("Root", "documento3.pdf", "archivo", "");
        arbol.addChildren("Root", "reporte.txt", "archivo", "");
        arbol.addChildren("Root", "reporteFinal.doc", "archivo", "");

        System.out.println("Buscando 'doc':");
        arbol.searchAutocomplete("doc");

        System.out.println("\nBuscando 'rep':");
        arbol.searchAutocomplete("rep");

        System.out.println("\nBuscando 'xyz' (no existe):");
        arbol.searchAutocomplete("xyz");

        System.out.println("--------------------------------------");
    }


    public static void main(String[] args) {
        System.out.println("===================================================");
        System.out.println("=      PRUEBAS INTEGRACIÓN- PROYECTO ÁRBOLES      =");
        System.out.println("=             Límites y Performance               =");
        System.out.println("===================================================");

        // ejecutar todo
        testCicloCompletoCRUD();
        testOperacionesComplejas();
        testCasosLimite();
        testArbolProfundo();
        testArbolAncho();
        testPerformanceGrande();
        testPersistenciaGrande();
        testPapeleraCompleja();
        testTrieAutocompletado();

        System.out.println("\n===================================================");
        System.out.println("=                      RESUMEN                      =");
        System.out.println("===================================================");
        System.out.println("Tests pasados: " + testsPasados);
        System.out.println("Tests fallados: " + testsFallados);


    }
}