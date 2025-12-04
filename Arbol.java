import java.util.List;
import java.util.ArrayList;
//import java.util.Collections; 

public class Arbol {

    private Node root; 
    private List<Node> trashCan; // papelera temporal

    public Arbol(String rootName) {
        this.root = new Node(rootName, "carpeta", null, null);
        this.trashCan = new ArrayList<>(); 
    }
    // busqueda de nodo por nombre (recursiva)
    public Node findNodeByName(String targetName, Node currentNode) {
        if (currentNode == null) {
            return null;
        }
        if (currentNode.getNombre().equals(targetName)) {
            return currentNode;
        }
        for (Node child : currentNode.getChildren()) {
            // busqueda recursiva
            Node result = findNodeByName(targetName, child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

//=============== OPERACIONES DEL ÁRBOL ===============
    // insertar Nodos
    public void addChildren(String parent, String child, String childtype, String content) {
        // usamos findNodeByName para encontrar tanto archivos como carpetas, pero validamos que sea carpeta
        Node folder = findNodeByName(parent, this.root); 
        
        // también se debe validar que el nodo hijo no exista ya en la carpeta
        if (folder != null && "carpeta".equals(folder.getTipo())) {
            Node newNode = new Node(child, childtype, content, folder);
            folder.getChildren().add(newNode);
             System.out.println("Nodo añadido: " + newNode.getNombre() + " bajo la carpeta: " + folder.getNombre());
        } else {
             System.out.println("Error. Carpeta no encontrada o tipo inválido.");
        }
    }
    
    // eliminar Nodo (mover a papelera)
    public boolean removeNode(String targetName) {
        Node target = findNodeByName(targetName, this.root);

        if (target == null || target.equals(this.root)) {
            System.out.println("Error. Nodo no existe o no se puede eliminar la raíz.");
            return false;
        }

        Node parent = target.getParent();
        if (parent != null) {
            // desconectar del padre
            parent.getChildren().remove(target);
            
            // mover a la papelera 
            this.trashCan.add(target);
            target.setParent(null); // desconecta la referencia al padre
            
            System.out.println("Nodo '" + targetName + "' movido a la papelera.");
            return true;
        }
        return false;
    }
    
    // mover Nodo
    public boolean moveNode(String targetName, String destinationName) {
        Node target = findNodeByName(targetName, this.root); 
        Node destination = findNodeByName(destinationName, this.root); 

        if (target == null || destination == null || target.equals(this.root) || !destination.getTipo().equals("carpeta")) {
            System.out.println("Error al mover. Origen/destino inválido.");
            return false;
        }

        Node oldParent = target.getParent();
        oldParent.getChildren().remove(target); // desconectar

        destination.getChildren().add(target);
        target.setParent(destination); // reconectar

        System.out.println("Nodo '" + targetName + "' movido a: " + destinationName);
        return true;
    }

    // renombrar Nodo
    public boolean renameNode(String oldName, String newName) {
        Node target = findNodeByName(oldName, this.root);

        if (target == null || newName.isEmpty()) {
            System.out.println("Error. Nodo '" + oldName + "' no encontrado o nombre inválido.");
            return false;
        }
        target.setNombre(newName);
        System.out.println("Nodo '" + oldName + "' renombrado a: " + newName);
        return true;
    }

    // ======== MEDICIONES DEL ÁRBOL ========
 // calcular altura
    public int calculateHeight(Node node) {
        if (node == null || node.getChildren().isEmpty()) {
            return 0;
        }
        int maxHeight = 0;
        for (Node child : node.getChildren()) {
            maxHeight = Math.max(maxHeight, calculateHeight(child));
        }
        return 1 + maxHeight;
    }
    // obtener altura del árbol
    public int getHeight() {
        return calculateHeight(this.root);
    }
// calcular tamaño
    public int calculateSize(Node node) {
        if (node == null) {
            return 0;
        }
        int size = 1;
        for (Node child : node.getChildren()) {
            size += calculateSize(child);
        }
        return size;
    }
    // obtener tamaño del árbol
    public int getSize() {
        return calculateSize(this.root);
    }
    
    //========== VISUALIZACIÓN DEL ÁRBOL ==========
// mostrar estructura del árbol
    public void displayTree() {
        System.out.println("\n=== ESTRUCTURA DEL ÁRBOL ===");
        identar(this.root, "");
        System.out.println("============================");
    }
   // metodo recursivo para indentar y mostrar
    private void identar(Node node, String indent) {
        String typeIndicator = node.getTipo().equals("carpeta") ? "[DIR]" : "[FILE]";
        System.out.println(indent + typeIndicator + " " + node.getNombre());
        for (Node child : node.getChildren()) {
            identar(child, indent + "  "); 
        }
    }
    
    // -============ PRUEBAS UNITARIAS DEL ÁRBOL =============
    public static void main(String[] args) {
        Arbol miArbol = new Arbol("/");
        System.out.println("Árbol inicializado.");
        
        // ============ PRUEBA DE INSERCIÓN Y TAMAÑO =============
        miArbol.addChildren("/", "Docs", "carpeta", null);
        miArbol.addChildren("Docs","info.pdf", "archivo", "Contenido A.");
        miArbol.addChildren("/", "Fotos", "carpeta", null);
        miArbol.addChildren("Fotos","vacaciones.jpg", "archivo", "Contenido B.");
        
        int expectedSize = 5;
        int expectedHeight = 2;
      
        System.out.println("\n--- PRUEBA INICIAL ---");
        System.out.println("Tamaño esperado: " + expectedSize + " | Tamaño real: " + miArbol.getSize()); 
        System.out.println("Altura esperada: " + expectedHeight + " | Altura real: " + miArbol.getHeight()); 
        
        if (miArbol.getSize() == expectedSize && miArbol.getHeight() == expectedHeight) {
             System.out.println("Tamaño y Altura correctos.");
        } else {
             System.out.println("Tamaño o Altura incorrectos.");
        }
        miArbol.displayTree();
        
        // ============= PRUEBA DE RENOMBRAR Y MOVER ================ 
        miArbol.renameNode("Fotos", "Images"); // renombrar
        miArbol.moveNode("vacaciones.jpg", "Docs"); // mover archivo a Docs
        
        // ================= PRUEBA DE ELIMINACIÓN ===================
        miArbol.removeNode("Docs"); // elimina Docs y su contenido 
        
        int expectedSizeAfterDelete = 2;
        int expectedTrashSize = 3; 

        System.out.println("\n--- PRUEBA DE ELIMINACIÓN ---");
        System.out.println("Tamaño después de eliminar Docs: " + miArbol.getSize());
        System.out.println("Elementos en papelera: " + miArbol.trashCan.size());
        
        if (miArbol.getSize() == expectedSizeAfterDelete && miArbol.trashCan.size() == expectedTrashSize) {
             System.out.println("Move/Delete correctos.");
        } else {
             System.out.println("Move/Delete incorrectos.");
        }

        miArbol.displayTree();
    }
}

