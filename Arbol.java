import java.util.List;
import java.util.Scanner;



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
	    
	    //exportar arbol a archivo .json
	    public void exportToJSON(String rutaArchivo) {
	        try (FileWriter writer = new FileWriter(rutaArchivo)) {
	            gson.toJson(this.root, writer);
	            System.out.println("✅ Árbol exportado: " + rutaArchivo);
	        } catch (IOException e) {
	            System.out.println("❌ Error al exportar: " + e.getMessage());
	        }
	    }
	    
	    
	    public String exportarArbolToString() {
	        return gson.toJson(this.root);
	    }
	    //exportar la papelera a un json
	    public void exportTrashCanToJSON(String nombreArchivo) {
	        try (FileWriter writer = new FileWriter(nombreArchivo)) {
	            Gson gson = new GsonBuilder().setPrettyPrinting().create();
	            gson.toJson(this.trashCan, writer);
	            
	            System.out.println("Papelera exportada: " + nombreArchivo);
	            System.out.println("Elementos: " + trashCan.size());
	            
	        } catch (IOException e) {
	            System.out.println("Error al exportar: " + e.getMessage());
	        }
	    }
	    
	    public void importTrashCanToJSON(String nombreArchivo) {
	        try (FileReader reader = new FileReader(nombreArchivo)) {
	            Gson gson = new Gson();
	            java.lang.reflect.Type tipoLista = 
	                new com.google.gson.reflect.TypeToken<List<Node>>(){}.getType();
	            
	            List<Node> elementosImportados = gson.fromJson(reader, tipoLista);
	            
	            if (elementosImportados == null) {
	                System.out.println("⚠Archivo vacío o inválido");
	                return;
	            }
	            
	            // Añadir elementos importados a la papelera actual
	            int agregados = 0;
	            for (Node nodo : elementosImportados) {
	                this.trashCan.add(nodo);
	                agregados++;
	            }
	            
	            System.out.println("Papelera importada: " + nombreArchivo);
	            System.out.println("Elementos añadidos: " + agregados);
	            System.out.println("Total en papelera: " + trashCan.size());
	            
	        } catch (FileNotFoundException e) {
	            System.out.println(" Archivo no encontrado: " + nombreArchivo);
	        } catch (IOException e) {
	            System.out.println("❌ Error al leer archivo: " + e.getMessage());
	        }
	    }
	    
	    public void CleanTrashCan() {
	        int cantidad = trashCan.size();
	        trashCan.clear();
	        System.out.println("Papelera vaciada");
	        System.out.println("Elementos eliminados: " + cantidad);
	    }
	    
	    //importar arbol de archivo .json
	    public void importFromJSON(String rutaArchivo) {
	        try (FileReader reader = new FileReader(rutaArchivo)) {
	            Node nuevoArbol = gson.fromJson(reader, Node.class);
	            
	            if (nuevoArbol == null) {
	                throw new IOException("El archivo JSON está vacío o es inválido");
	            }
	            
	            this.root = nuevoArbol;
	            reconstruirPadres(this.root, null);
	            
	            System.out.println("✅ Árbol importado: " + rutaArchivo);
	        } catch (FileNotFoundException e) {
	            System.out.println("❌ Archivo no encontrado: " + rutaArchivo);
	        } catch (IOException e) {
	            System.out.println("❌ Error: " + e.getMessage());
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
	            
	            System.out.println("✅ Árbol importado desde string");
	        } catch (Exception e) {
	            System.out.println("❌ Error al importar: " + e.getMessage());
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
	    	Scanner input = new Scanner(System.in);
	    	
	        Arbol miArbol = new Arbol("/");
	        
	        while (true) {
	        	System.out.println("------------MENU-------------");
	            System.out.println("1 - mkdir");
	            System.out.println("2 - touch");
	            System.out.println("3 - rm");
	            System.out.println("4 - mv");
	            System.out.println("5 - search");
	            System.out.println("6 - height");
	            System.out.println("7 - size");
	            System.out.println("8 - export");
	            System.out.println("9 - help");
	            System.out.println("10 - exit");
	            System.out.println("0 - show");
	            System.out.print(" ");
	            
	            String opcion = input.nextLine();
	            
	            switch (opcion) {
	                case "mkdir":
	                	// Creación e indexación (Día 2 & Día 5)
	                	System.out.println("Creando carpetas/archivos...");
	        	        miArbol.addChildren("/", "Documentos", "carpeta", null);
	        	        miArbol.addChildren("Documentos","informe.pdf", "archivo", "Contenido A.");
	        	        miArbol.addChildren("Documentos","resumen.txt", "archivo", "Contenido B.");
	        	        miArbol.addChildren("/", "Reportes", "carpeta", null);
	        	        
	                    //System.out.print("insert ");
	                    //int valueInsert = Integer.parseInt(input.nextLine());
	                    // arbol.insert(valueInsert);
	                    //System.out.println("Insertado: " + valueInsert);
	                    break;
	                    
	                case "touch":
	                	System.out.println("Copiar...");
	                    //System.out.print("search ");
	                    //int valueSearch = Integer.parseInt(input.nextLine());
	                    // arbol.search(valueSearch);
	                    //System.out.println("Buscando: " + valueSearch);
	                    break;
	                    
	                case "rm":
	                	
	                	 // ================= PRUEBA DE ELIMINACIÓN ===================
	        	        miArbol.removeNode("Docs"); // elimina Docs y su contenido
	        	        miArbol.exportTrashCanToJSON("TrashCan.json");
	                    //System.out.print("delete ");
	                    //int valueDelete = Integer.parseInt(input.nextLine());
	                    // arbol.delete(valueDelete);
	                    //System.out.println("Eliminando: " + valueDelete);
	                    break;
	                    
	                case "mv":
	                    System.out.println("re) RENOMBRAR");
	                    System.out.println("mov) MOVER");
	                    System.out.print("Ingrese la orden que desea: ");
	                    String orden = input.nextLine();
	                    
	                    switch (orden) {
	                        case "re":
	                        	miArbol.renameNode("Fotos", "Images"); // renombrar
	                        	miArbol.renameNode("Reportes", "Informes");
	                        	miArbol.renameNode("Documentos", "Docs");
	                            // arbol.inorder();
	                            break;
	                        case "mov":
	                        	miArbol.moveNode("vacaciones.jpg", "Docs"); // mover archivo a Docs
	                        	miArbol.moveNode("resumen.txt", "Informes");
	                            // arbol.preorder();
	                            break;
	                        
	                        default:
	                            System.out.println("Opción no válida");
	                    }
	                    break;
	                    
	                case "5":
	                    // System.out.println("Altura del árbol: " + arbol.height());
	                    System.out.println("Altura del árbol: [implementar]");
	                    break;
	                    
	                case "6":
	                    // System.out.println("Tamaño del arbol: " + arbol.size());
	                    System.out.println("Tamaño del arbol: [implementar]");
	                    break;
	                    
	                case "export":
	                    System.out.println("exp) EXPORTAR");
	                    System.out.println("imp) IMPORTAR");
	                    System.out.print("Ingrese la opcion que desea: ");
	                    String choice = input.nextLine();
	                    
	                    switch (choice) {
	                        case "exp":
	                            // arbol.export("arbol.txt");
	                            System.out.println("Exportando...");
	                            miArbol.exportToJSON("Organizacion.json"); 
	                            
	                            break;
	                        case "imp":
	                            // arbol.load("arbol.txt");
	                            System.out.println("Importando...");
	                            miArbol.importFromJSON("Organizacion.json");
	                            break;
	                        default:
	                            System.out.println("Opción no válida");
	                    }
	                    break;
	                    
	                case "help":
	                    System.out.println("\n--- AYUDA ---");
	                    System.out.println("Este programa implementa un árbol binario de búsqueda.");
	                    System.out.println("Características:");
	                    System.out.println("- Los valores menores van a la izquierda");
	                    System.out.println("- Los valores mayores van a la derecha");
	                    System.out.println("\nOperaciones disponibles:");
	                    System.out.println("1. insert: Agrega un nuevo valor al árbol");
	                    System.out.println("2. search: Encuentra un valor en el árbol");
	                    System.out.println("3. delete: Remueve un valor del árbol");
	                    System.out.println("4. order: Muestra los valores en diferente orden");
	                    System.out.println("4.1. in: Izquierda/Raiz/Derecha");
	                    System.out.println("4.2. pre: Raiz/Izquierda/Derecha");
	                    System.out.println("4.3. post: Izquierda/Derecha/Raiz");
	                    System.out.println("5. height: Muestra la altura del árbol");
	                    System.out.println("6. size: Muestra el número de nodos");
	                    System.out.println("7. export: Exporta el árbol a un formato");
	                    System.out.println("7.1. exp: Exporta el arbol a un formato");
	                    System.out.println("7.2. imp: Importa el arbol a un formato");
	                    System.out.println("8. help: Menu de ayuda");
	                    System.out.println("9. exit: Termina el programa");
	                    break;
	                    
	                case "exit":
	                    System.out.println("Programa terminado");
	                    input.close();
	                    return;
	                case "show":
	                	miArbol.displayTree();
	                	break;
	                    
	                default:
	                    System.out.println("Opción no válida. Intente de nuevo.");
	        }
	        
	        
	        
	    	
	    	
	        
	        // Pruebas de Consistencia (Día 3)
	        System.out.println("\n--- CONSISTENCIA ---");
	        System.out.println("Tamaño: " + miArbol.getSize()); 
	        System.out.println("Altura: " + miArbol.getHeight()); 

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
	        
	        
	        
	        
	        
	        //int expectedSizeAfterDelete = 2;
	        //int expectedTrashSize = 3; 

	        // Prueba de Búsqueda (Día 6)
	        miArbol.searchAutocomplete("doc");
	        miArbol.searchAutocomplete("res"); 
	        miArbol.searchAutocomplete("repo"); 

	        // Prueba de Operaciones (Día 3)
	        
	        
	        
	        // Prueba de Persistencia (Día 4)
	       

	        miArbol.displayTree();
	    }
	    }
}