import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arbol {

    private Node root; 
    private List<Node> trashCan; 
    private Trie nameTrie;     

    public Arbol(String rootName) {
        this.root = new Node(rootName, "carpeta", null, null);
        this.trashCan = new ArrayList<>(); 
        this.nameTrie = new Trie(); 
        this.nameTrie.insert(rootName, this.root.getId()); 
    }

    // funciones de búsqueda auxiliar
    
    public Node findNodeByName(String targetName, Node currentNode) {
        if (currentNode == null) return null;
        if (currentNode.getNombre().equals(targetName)) return currentNode;
        
        for (Node child : currentNode.getChildren()) {
            Node result = findNodeByName(targetName, child);
            if (result != null) return result;
        }
        return null;
    }

    // ================= OPERACIONES DE ÁRBOLES ==================

    // insertar Nodos con trie
    public void addChildren(String parent, String child, String childtype, String content) {
        Node folder = findNodeByName(parent, this.root);
        
        if (folder != null && "carpeta".equals(folder.getTipo())) {
            Node newNode = new Node(child, childtype, content, folder);
            folder.getChildren().add(newNode);
            
            this.nameTrie.insert(newNode.getNombre(), newNode.getId()); 
            
        } 
    }
    
    // eliminar nodo con papelera
    public boolean removeNode(String targetName) {
        Node target = findNodeByName(targetName, this.root);

        if (target == null || target.equals(this.root)) return false;

        Node parent = target.getParent();
        if (parent != null) {
            parent.getChildren().remove(target);
            this.trashCan.add(target);
            target.setParent(null); 
            
            // aún no ponemos en el trie la eliminación
            
            return true;
        }
        return false;
    }
    
    // mover Nodo
    public boolean moveNode(String targetName, String destinationName) {
        Node target = findNodeByName(targetName, this.root); 
        Node destination = findNodeByName(destinationName, this.root); 

        if (target == null || destination == null || target.equals(this.root) || !destination.getTipo().equals("carpeta")) {
            return false;
        }

        Node oldParent = target.getParent();
        oldParent.getChildren().remove(target); 

        destination.getChildren().add(target);
        target.setParent(destination); 

        return true;
    }

    // renombrar Nodo
    public boolean renameNode(String oldName, String newName) {
        Node target = findNodeByName(oldName, this.root);

        if (target == null || newName.isEmpty()) return false;
        
        //igual aquí, aun no actualizamos el trie
        
        target.setNombre(newName);
        return true;
    }

    // ================= CÁLCULOS DE ÁRBOLES  ==================

    // altura
    public int calculateHeight(Node node) {
        if (node == null || node.getChildren().isEmpty()) return 0;
        int maxHeight = 0;
        for (Node child : node.getChildren()) {
            maxHeight = Math.max(maxHeight, calculateHeight(child));
        }
        return 1 + maxHeight;
    }
    public int getHeight() { return calculateHeight(this.root); }

    // tamaño
    public int calculateSize(Node node) {
        if (node == null) return 0;
        int size = 1;
        for (Node child : node.getChildren()) {
            size += calculateSize(child);
        }
        return size;
    }
    public int getSize() { return calculateSize(this.root); }

    // ================= PERSISTENCIA JSON ====================

    public void exportToJson(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(toJsonString(this.root, 0));
            System.out.println(" Árbol exportado a: " + filename);
        } catch (IOException e) {
            System.out.println("Error al exportar: " + e.getMessage());
        }
    }
    
    // implemnentación del JSON
    private String toJsonString(Node node, int indent) {
        StringBuilder sb = new StringBuilder();
        String spaces = "  ".repeat(indent);
        
        sb.append(spaces).append("{\n");
        sb.append(spaces).append("  \"id\": \"").append(escapeJson(node.getId())).append("\",\n");
        sb.append(spaces).append("  \"nombre\": \"").append(escapeJson(node.getNombre())).append("\",\n");
        sb.append(spaces).append("  \"tipo\": \"").append(escapeJson(node.getTipo())).append("\",\n");
        sb.append(spaces).append("  \"contenido\": ");
        
        if (node.getContenido() == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(escapeJson(node.getContenido())).append("\"");
        }
        
        sb.append(",\n");
        sb.append(spaces).append("  \"children\": [\n");
        
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            sb.append(toJsonString(children.get(i), indent + 2));
            if (i < children.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        sb.append(spaces).append("  ]\n");
        sb.append(spaces).append("}");
        
        return sb.toString();
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
    }
    
    // ================= BÚSQUEDA Y AUTOCOMPLETADO CON TRIE ==================
    
    public void searchAutocomplete(String prefix) {
        if (this.nameTrie == null) return;
        
        List<String> results = this.nameTrie.autocomplete(prefix);
        
        System.out.println("\n--- AUTOCOMPLETADO para '" + prefix + "' ---");
        if (results.isEmpty()) {
            System.out.println("No se encontraron coincidencias.");
        } else {
            for (String result : results) {
                System.out.println("  " + result);
            }
        }
    }

    // ================== VISUALIZACIÓN DEL ÁRBOL Y PRUEBAS UNITARIAS ==================
    
    public void displayTree() {
        System.out.println("\n=== ESTRUCTURA DEL ÁRBOL ===");
        identar(this.root, "");
        System.out.println("============================");
    }
    
    private void identar(Node node, String indent) {
        String typeIndicator = node.getTipo().equals("carpeta") ? "[DIR]" : "[FILE]";
        System.out.println(indent + typeIndicator + " " + node.getNombre());
        for (Node child : node.getChildren()) {
            identar(child, indent + "  "); 
        }
    }
    
    public static void main(String[] args) {
        Arbol miArbol = new Arbol("/");
        
        // Creación e indexación (Día 2 & Día 5)
        miArbol.addChildren("/", "Documentos", "carpeta", null);
        miArbol.addChildren("Documentos","informe.pdf", "archivo", "Contenido A.");
        miArbol.addChildren("Documentos","resumen.txt", "archivo", "Contenido B.");
        miArbol.addChildren("/", "Reportes", "carpeta", null);
        
        // Pruebas de Consistencia (Día 3)
        System.out.println("\n--- CONSISTENCIA ---");
        System.out.println("Tamaño: " + miArbol.getSize()); 
        System.out.println("Altura: " + miArbol.getHeight()); 

        // Prueba de Búsqueda (Día 6)
        miArbol.searchAutocomplete("doc");
        miArbol.searchAutocomplete("res"); 
        miArbol.searchAutocomplete("repo"); 

        // Prueba de Operaciones (Día 3)
        miArbol.renameNode("Reportes", "Informes");
        miArbol.moveNode("resumen.txt", "Informes");
        
        // Prueba de Persistencia (Día 4)
        miArbol.exportToJson("Organizacion.json"); 

        miArbol.displayTree();
    }
}