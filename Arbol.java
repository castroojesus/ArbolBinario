//package ProyectoArboles;

import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class Arbol {
    Node root;
    private List<Node> trashCan;
    private Trie nameTrie;
    private Map<String, Node> nodeMap; 
    private final Gson gson;

    public Arbol(String rootName) {
        this.root = new Node(rootName, "carpeta", null, null);
        this.trashCan = new ArrayList<>();
        this.nameTrie = new Trie();
        this.nodeMap = new HashMap<>();
        
        this.nameTrie.insert(rootName, this.root.getId());
        this.nodeMap.put(this.root.getId(), this.root); 
        
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    // obtener nodo por id (búsqueda exacta con Mapa Hash)
    public Node findNodeById(String id) {
        return nodeMap.get(id);
    }
    
    public Node findNodeByName(String targetName, Node currentNode) {
        if (currentNode == null) return null;
        if (currentNode.getNombre().equals(targetName)) return currentNode;
        
        for (Node child : currentNode.getChildren()) {
            Node result = findNodeByName(targetName, child);
            if (result != null) return result;
        }
        return null;
    }

    // ================= OPERACIONES ÁRBOLES ==================

    public boolean addChildren(String parent, String child, String childtype, String content) {
        if (child == null || child.trim().isEmpty()) {
            System.out.println("Error: El nombre no puede estar vacío");
            return false;
        }
        
        if (!childtype.equalsIgnoreCase("carpeta") && !childtype.equalsIgnoreCase("archivo")) {
            System.out.println("Error: Tipo debe ser 'carpeta' o 'archivo'");
            return false;
        }
        
        Node folder = findNodeByName(parent, this.root);
        
        if (folder == null) {
            System.out.println("Error: No se encontró la carpeta padre '" + parent + "'");
            return false;
        }
        
        if (!"carpeta".equals(folder.getTipo())) {
            System.out.println("Error: '" + parent + "' no es una carpeta");
            return false;
        }
        
        // verificar si ya existe un hijo con ese nombre
        for (Node existingChild : folder.getChildren()) {
            if (existingChild.getNombre().equals(child)) {
                System.out.println("Error: Ya existe '" + child + "' en '" + parent + "'");
                return false;
            }
        }
        
        Node newNode = new Node(child, childtype, content, folder);
        folder.getChildren().add(newNode);
        this.nameTrie.insert(newNode.getNombre(), newNode.getId());
        this.nodeMap.put(newNode.getId(), newNode); // añadir al Mapa Hash
        
        System.out.println("Creado exitosamente: " + child);
        return true;
    }
    
    public boolean removeNode(String targetName) {
        Node target = findNodeByName(targetName, this.root);

        if (target == null) {
            System.out.println("Error: No se encontró '" + targetName + "'");
            return false;
        }
        
        if (target.equals(this.root)) {
            System.out.println("Error: No se puede eliminar la raíz");
            return false;
        }

        Node parent = target.getParent();
        if (parent != null) {
            parent.getChildren().remove(target);
            this.trashCan.add(target);
            target.setParent(null);
            
            System.out.println("Eliminado: " + targetName + " (movido a papelera)");
            return true;
        }
        return false;
    }
    
    public boolean moveNode(String targetName, String destinationName) {
        Node target = findNodeByName(targetName, this.root);
        Node destination = findNodeByName(destinationName, this.root);

        if (target == null || destination == null) {
            System.out.println("Error: origen o destino no encontrado");
            return false;
        }
        
        if (target.equals(this.root)) {
            System.out.println("Error: No se puede mover la raíz");
            return false;
        }
        
        if (!destination.getTipo().equals("carpeta")) {
            System.out.println("Error: El destino debe ser una carpeta");
            return false;
        }

        Node oldParent = target.getParent();
        oldParent.getChildren().remove(target);
        destination.getChildren().add(target);
        target.setParent(destination);

        System.out.println("Movido: " + targetName + " -> " + destinationName);
        return true;
    }

    public boolean renameNode(String oldName, String newName) {
        Node target = findNodeByName(oldName, this.root);

        if (target == null) {
            System.out.println("Error: No se encontró '" + oldName + "'");
            return false;
        }
        
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("Error: El nuevo nombre no puede estar vacío");
            return false;
        }
        
        Node parent = target.getParent();
        if (parent != null) {
            for (Node sibling : parent.getChildren()) {
                if (sibling.getNombre().equals(newName) && !sibling.equals(target)) {
                    System.out.println("Error: Ya existe '" + newName + "' en esta ubicación");
                    return false;
                }
            }
        }
        
        this.nameTrie.insert(newName, target.getId()); // actualizar Trie
        target.setNombre(newName);
        
        System.out.println("Renombrado: " + oldName + " -> " + newName);
        return true;
    }

    // ================= CÁLCULOS ==================

    public int calculateHeight(Node node) {
        if (node == null || node.getChildren().isEmpty()) return 0;
        int maxHeight = 0;
        for (Node child : node.getChildren()) {
            maxHeight = Math.max(maxHeight, calculateHeight(child));
        }
        return 1 + maxHeight;
    }
    
    public int getHeight() { 
        return calculateHeight(this.root); 
    }

    public int calculateSize(Node node) {
        if (node == null) return 0;
        int size = 1;
        for (Node child : node.getChildren()) {
            size += calculateSize(child);
        }
        return size;
    }
    
    public int getSize() { 
        return calculateSize(this.root); 
    }

    // ================= PERSISTENCIA ====================
    
    public boolean exportarArbol(String rutaArchivo) {
        List<String> recorrido = new ArrayList<>();
        recorridoPreorden(this.root, recorrido); // Obtiene el recorrido completo
        
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write("RECORRIDO PREORDEN:\n");
            for (String item : recorrido) {
                writer.write(item + "\n");
            }
            System.out.println("Árbol exportado (Preorden) a: " + rutaArchivo);
            return true;
        } catch (IOException e) {
            System.out.println("Error al exportar: " + e.getMessage());
            return false;
        }
    }
    
    // MÉTODO PARA EL EXPORT DE PREORDEN
    private void recorridoPreorden(Node node, List<String> recorrido) {
        if (node == null) return;

        // visitar el nodo
        recorrido.add(node.getNombre() + " (" + node.getTipo() + ")");

        // recorrer los hijos
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                recorridoPreorden(child, recorrido);
            }
        }
    }
    
    public boolean exportTrashCanToJSON(String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            gson.toJson(this.trashCan, writer);
            System.out.println("Papelera exportada: " + nombreArchivo);
            System.out.println("   Elementos: " + trashCan.size());
            return true;
        } catch (IOException e) {
            System.out.println("Error al exportar papelera: " + e.getMessage());
            return false;
        }
    }
    
    public boolean importFromJSON(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Node nuevoArbol = gson.fromJson(reader, Node.class);
            
            if (nuevoArbol == null) {
                throw new IOException("El archivo JSON está vacío o es inválido");
            }
            
            this.root = nuevoArbol;
            this.nameTrie = new Trie(); // limpiar Trie
            this.nodeMap = new HashMap<>(); // limpiar Mapa Hash
            
            reconstruirPadres(this.root, null);
            reconstruirIndices(this.root); 
            
            System.out.println("Árbol importado desde: " + rutaArchivo);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado: " + rutaArchivo);
            return false;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    public void CleanTrashCan() {
        int cantidad = trashCan.size();
        trashCan.clear();
        System.out.println("Papelera vaciada");
        System.out.println("   Elementos eliminados: " + cantidad);
    }
    
    private void reconstruirPadres(Node nodo, Node padre) {
        if (nodo == null) return;
        nodo.setParent(padre);
        
        if (nodo.getChildren() != null) {
            for (Node hijo : nodo.getChildren()) {
                reconstruirPadres(hijo, nodo);
            }
        }
    }
    
    private void reconstruirIndices(Node nodo) { 
        if (nodo == null) return;
        
        this.nameTrie.insert(nodo.getNombre(), nodo.getId());
        this.nodeMap.put(nodo.getId(), nodo); // rellenar Mapa Hash
        
        if (nodo.getChildren() != null) {
            for (Node hijo : nodo.getChildren()) {
                reconstruirIndices(hijo);
            }
        }
    }
    
    // ================= RUTA COMPLETA ==================
    
    public String getRutaCompleta(String targetName) {
        Node target = findNodeByName(targetName, this.root);
        if (target == null) {
            return "Error: Nodo no encontrado";
        }
        
        List<String> pathSegments = new ArrayList<>();
        Node current = target;
        
        while (current != null) {
            pathSegments.add(current.getNombre());
            current = current.getParent();
        }
        
        Collections.reverse(pathSegments);
        
        if (pathSegments.isEmpty()) return ""; 
        
        String fullPath = String.join("/", pathSegments);
        
        if (!this.root.getNombre().equals("/") && !fullPath.startsWith(this.root.getNombre())) {
            fullPath = "/" + fullPath;
        }
        
        return fullPath;
    }


    // ================= BÚSQUEDA Y AUTOCOMPLETADO ==================
    
    public void searchAutocomplete(String prefix) {
        if (this.nameTrie == null) return;
        
        List<String> results = this.nameTrie.autocomplete(prefix);
        
        System.out.println("\nAutocompletado para '" + prefix + "':");
        if (results.isEmpty()) {
            System.out.println("   No se encontraron coincidencias");
        } else {
            for (String result : results) {
                System.out.println("   - " + result);
            }
        }
    }

    // ================= VISUALIZACIÓN ==================
    
    public void displayTree() {
        System.out.println("\nESTRUCTURA DEL ÁRBOL");
        System.out.println("═══════════════════════════════");
        identar(this.root, "");
        System.out.println("═══════════════════════════════");
    }
    
    private void identar(Node node, String indent) {
        String icon = node.getTipo().equals("carpeta") ? "[DIR]" : "[FILE]";
        System.out.println(indent + icon + " " + node.getNombre());
        for (Node child : node.getChildren()) {
            identar(child, indent + "  ");
        }
    }
    
    public void showTrashCan() {
        System.out.println("\nPAPELERA DE RECICLAJE");
        System.out.println("═══════════════════════════════");
        if (trashCan.isEmpty()) {
            System.out.println("   (vacía)");
        } else {
            for (Node node : trashCan) {
                String icon = node.getTipo().equals("carpeta") ? "[DIR]" : "[FILE]";
                System.out.println("   " + icon + " " + node.getNombre());
            }
        }
        System.out.println("═══════════════════════════════");
    }

    // ================= MENÚ PRINCIPAL ==================
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Arbol miArbol = new Arbol("/");
        
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTIÓN DE ARCHIVOS     ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        while (true) {
            System.out.println("\n┌─────────── MENÚ PRINCIPAL ───────────┐");
            System.out.println("│ 1.  mkdir    - Crear carpeta         │");
            System.out.println("│ 2.  touch    - Crear archivo         │");
            System.out.println("│ 3.  rm       - Eliminar              │");
            System.out.println("│ 4.  mv       - Mover                 │");
            System.out.println("│ 5.  rename   - Renombrar             │");
            System.out.println("│ 6.  search   - Buscar/Autocompletar  │");
            System.out.println("│ 7.  tree     - Mostrar árbol         │");
            System.out.println("│ 8.  info     - Mostrar info/stats    │");
            System.out.println("│ 9.  trash    - Ver papelera          │");
            System.out.println("│ 10. export   - Exportar (Preorden)   │"); 
            System.out.println("│ 11. import   - Importar desde JSON   │");
            System.out.println("│ 12. test     - Ejecutar tests        │");
            System.out.println("│ 13. path     - Mostrar ruta completa │"); 
            System.out.println("│ 14. help     - Ayuda                 │");
            System.out.println("│ 0.  exit     - Salir                 │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Comando: ");
            
            String opcion = input.nextLine().trim().toLowerCase();
            
            switch (opcion) {
                case "1": case "mkdir":
                    System.out.print("Carpeta padre: ");
                    String padre = input.nextLine().trim();
                    System.out.print("Nombre de la nueva carpeta: ");
                    String nombreCarpeta = input.nextLine().trim();
                    miArbol.addChildren(padre, nombreCarpeta, "carpeta", null);
                    break;
                    
                case "2": case "touch":
                    System.out.print("Carpeta padre: ");
                    String padreArchivo = input.nextLine().trim();
                    System.out.print("Nombre del archivo: ");
                    String nombreArchivo = input.nextLine().trim();
                    System.out.print("Contenido (opcional): ");
                    String contenido = input.nextLine();
                    miArbol.addChildren(padreArchivo, nombreArchivo, "archivo", 
                                       contenido.isEmpty() ? null : contenido);
                    break;
                    
                case "3": case "rm":
                    System.out.print("Nombre del elemento a eliminar: ");
                    String aEliminar = input.nextLine().trim();
                    miArbol.removeNode(aEliminar);
                    break;
                    
                case "4": case "mv":
                    System.out.print("Elemento a mover: ");
                    String origen = input.nextLine().trim();
                    System.out.print("Carpeta destino: ");
                    String destino = input.nextLine().trim();
                    miArbol.moveNode(origen, destino);
                    break;
                    
                case "5": case "rename":
                    System.out.print("Nombre actual: ");
                    String nombreViejo = input.nextLine().trim();
                    System.out.print("Nuevo nombre: ");
                    String nombreNuevo = input.nextLine().trim();
                    miArbol.renameNode(nombreViejo, nombreNuevo);
                    break;
                    
                case "6": case "search":
                    System.out.print("Prefijo a buscar: ");
                    String prefijo = input.nextLine().trim();
                    miArbol.searchAutocomplete(prefijo);
                    break;
                    
                case "7": case "tree":
                    miArbol.displayTree();
                    break;
                    
                case "8": case "info":
                    System.out.println("\nINFORMACIÓN DEL ÁRBOL");
                    System.out.println("═══════════════════════════════");
                    System.out.println("Tamaño total: " + miArbol.getSize() + " nodos");
                    System.out.println("Altura: " + miArbol.getHeight() + " niveles");
                    System.out.println("Elementos en papelera: " + miArbol.trashCan.size());
                    System.out.println("═══════════════════════════════");
                    break;
                    
                case "9": case "trash":
                    miArbol.showTrashCan();
                    System.out.print("\n¿Vaciar papelera? (s/n): ");
                    String resp = input.nextLine().trim().toLowerCase();
                    if (resp.equals("s") || resp.equals("si")) {
                        miArbol.CleanTrashCan();
                    }
                    break;
                    
                case "10": case "export": 
                    System.out.print("Nombre del archivo de exportación (ej: arbol.txt): ");
                    String archivoExp = input.nextLine().trim();
                    if (archivoExp.isEmpty()) archivoExp = "arbol.txt";
                    miArbol.exportarArbol(archivoExp);
                    
                    System.out.print("¿Exportar papelera también a JSON? (s/n): ");
                    String expTrash = input.nextLine().trim().toLowerCase();
                    if (expTrash.equals("s") || expTrash.equals("si")) {
                        miArbol.exportTrashCanToJSON("papelera.json");
                    }
                    break;
                    
                case "11": case "import":
                    System.out.print("Nombre del archivo a importar: ");
                    String archivoImp = input.nextLine().trim();
                    if (!archivoImp.isEmpty()) {
                        miArbol.importFromJSON(archivoImp);
                    }
                    break;
                    
                case "12": case "test":
                    System.out.println("\nEjecutando tests de integración...\n");
                    TestIntegracion.main(null);
                    break;
                    
                case "13": case "path":
                    System.out.print("Nombre del nodo: ");
                    String nodoRuta = input.nextLine().trim();
                    System.out.println("Ruta: " + miArbol.getRutaCompleta(nodoRuta));
                    break;
                    
                case "14": case "help":
                    mostrarAyuda();
                    break;
                    
                case "0": case "exit": case "salir":
                    System.out.println("\nBYE...");
                    input.close();
                    return;
                    
                default:
                    System.out.println("Comando no reconocido. Escribe 'help' para ver la ayuda.");
            }
        }
    }
    
    private static void mostrarAyuda() {
        System.out.println("\nAYUDA");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Este programa implementa un árbol para gestión de archivos.");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("\nCOMANDOS PRINCIPALES:");
        System.out.println("  mkdir   - Crea una nueva carpeta");
        System.out.println("  touch   - Crea un nuevo archivo");
        System.out.println("  rm      - Elimina un elemento (va a papelera)");
        System.out.println("  mv      - Mueve un elemento a otra carpeta");
        System.out.println("  rename  - Cambia el nombre de un elemento");
        System.out.println("  path    - Muestra la ruta completa del nodo");
        System.out.println("  search  - Busca elementos con autocompletado");
        System.out.println("\nVISUALIZACIÓN Y PERSISTENCIA:");
        System.out.println("  tree    - Muestra la estructura completa");
        System.out.println("  info    - Estadísticas del árbol");
        System.out.println("  trash   - Ver/vaciar papelera de reciclaje");
        System.out.println("  export  - Guarda el árbol en formato PREORDEN"); 
        System.out.println("  import  - Carga un árbol desde JSON");
        System.out.println("\nCARACTERÍSTICAS:");
        System.out.println("  - Autocompletado con Trie");
        System.out.println("  - Búsqueda exacta con Mapa Hash (por ID)");
        System.out.println("  - Persistencia completa en JSON");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}