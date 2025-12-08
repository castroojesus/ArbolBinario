import java.util.List;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



public class Arbol {
	 private Node root; 
	    private List<Node> trashCan; 
	    private Trie nameTrie;  
	    private final Gson gson;

	    public Arbol(String rootName) {
	        this.root = new Node(rootName, "carpeta", null, null);
	        this.trashCan = new ArrayList<>(); 
	        this.nameTrie = new Trie(); 
	        this.nameTrie.insert(rootName, this.root.getId()); 
	        this.gson = new GsonBuilder()
	                .setPrettyPrinting()  // JSON bien formateado
	                .serializeNulls()     // Mostrar campos nulos
	                .create();
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

	    public void exportToJSON(String rutaArchivo) {
	        try (FileWriter writer = new FileWriter(rutaArchivo)) {
	            gson.toJson(this.root, writer);
	            System.out.println("Árbol exportado: " + rutaArchivo);
	        } catch (IOException e) {
	            System.out.println(" Error al exportar: " + e.getMessage());
	        }
	    }
	    
	    
	    public String exportarArbolToString() {
	        return gson.toJson(this.root);
	    }
	    
	    public void importFromJSON(String rutaArchivo) {
	        try (FileReader reader = new FileReader(rutaArchivo)) {
	            Node nuevoArbol = gson.fromJson(reader, Node.class);
	            
	            if (nuevoArbol == null) {
	                throw new IOException("El archivo JSON está vacío o es inválido");
	            }
	            
	            this.root = nuevoArbol;
	            reconstruirPadres(this.root, null);
	            
	            System.out.println("Árbol importado: " + rutaArchivo);
	        } catch (FileNotFoundException e) {
	            System.out.println("Archivo no encontrado: " + rutaArchivo);
	        } catch (IOException e) {
	            System.out.println("Error: " + e.getMessage());
	        }
	    }
	    
	    
	    public void importarArbolFromString(String jsonString) {
	        try {
	            Node nuevoArbol = gson.fromJson(jsonString, Node.class);
	            
	            if (nuevoArbol == null) {
	                throw new IllegalArgumentException("String JSON inválido");
	            }
	            
	            this.root = nuevoArbol;
	            reconstruirPadres(this.root, null);
	            
	            System.out.println(" Árbol importado desde string");
	        } catch (Exception e) {
	            System.out.println(" Error al importar: " + e.getMessage());
	        }
	    }
	    
	    // ============= MÉTODOS AUXILIARES =============
	    
	    private void reconstruirPadres(Node nodo, Node padre) {
	        if (nodo == null) return;
	        nodo.setParent(padre);
	        
	        if (nodo.getChildren() != null) {
	            for (Node hijo : nodo.getChildren()) {
	                reconstruirPadres(hijo, nodo);
	            }
	        }
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
	        //interfaz grafica v1.0 (Dia 7)
	    	JFrame window = new JFrame();
	    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	window.setResizable(false);
	    	window.setTitle("CMD");
	    	
	        Arbol miArbol = new Arbol("/");
	        
	        // Creación e indexación (Día 2 & Día 5)
	        miArbol.addChildren("/", "Documentos", "carpeta", null);
	        miArbol.addChildren("Documentos","informe.pdf", "archivo", "Contenido A.");
	        miArbol.addChildren("Documentos","resumen.txt", "archivo", "Contenido B.");
	        miArbol.addChildren("/", "Reportes", "carpeta", null);
	        GamePanel gamePanel=new GamePanel(miArbol);
	        window.add(gamePanel);
	    	
	    	window.pack();
	    	window.setLocationRelativeTo(null);
	    	window.setVisible(true);
	    	gamePanel.startGameThread();
	        
	        System.out.println("Sistema iniciado correctamente");
	        System.out.println("   Usa F1 en la ventana para ayuda");
	        
	        // Pruebas de Consistencia (Día 3)
	        System.out.println("\n--- CONSISTENCIA ---");
	        System.out.println("Tamaño: " + miArbol.getSize()); 
	        System.out.println("Altura: " + miArbol.getHeight()); 

	        int expectedSize = 5;
	        int expectedHeight = 2;
	      
	        System.out.println("\n--- PRUEBA INICIAL ---");
	        System.out.println("Tamaño esperado: " + expectedSize + " | Tamaño real: " + miArbol.getSize()); 
	        System.out.println("Altura esperada: " + expectedHeight + " | Altura real: " + miArbol.getHeight()); 

	        miArbol.exportToJSON("Organizacion.json");
	        
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
	        
	        //int expectedSizeAfterDelete = 2;
	        //int expectedTrashSize = 3; 

	        // Prueba de Búsqueda (Día 6)
	        miArbol.searchAutocomplete("doc");
	        miArbol.searchAutocomplete("res"); 
	        miArbol.searchAutocomplete("repo"); 

	        // Prueba de Operaciones (Día 3)
	        miArbol.renameNode("Reportes", "Informes");
	        miArbol.moveNode("resumen.txt", "Informes");
	        
	        // Prueba de Persistencia (Día 4)
	        miArbol.exportToJSON("Organizacion.json"); 

	        miArbol.displayTree();
	    }
	}