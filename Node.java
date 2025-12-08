import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Node {
	    private String id;          // Identificador único
	    private String nombre;      // Nombre del nodo
	    private String tipo;        // "carpeta" o "archivo"
	    private String contenido;   // Solo para archivos
	    private List<Node> children; // Hijos del nodo
	    private transient Node parent; // Nodo padre (transient para evitar serialización)
	    
	    public Node() {
	        this.id = UUID.randomUUID().toString();
	        this.children = new ArrayList<>();
	    }

	    public Node(String nombre, String tipo, String contenido, Node parent) {
	        this.id = UUID.randomUUID().toString();
	        this.nombre = nombre;
	        this.tipo = tipo.toLowerCase();
	        this.parent = parent;
	        this.children = new ArrayList<>();

	        if ("archivo".equals(this.tipo)) {
	            this.contenido = contenido;
	        } else {
	            this.contenido = null;
	        }
	    }

	    // --- Getters y Setters ---

	    public String getId() { return id; }
	    public String getNombre() { return nombre; }
	    public void setNombre(String nombre) { this.nombre = nombre; }
	    public String getTipo() { return tipo; }
	    public String getContenido() { return contenido; }
	    public void setContenido(String contenido) { this.contenido = contenido; }
	    public List<Node> getChildren() { return children; }
	    public Node getParent() { return parent; }
	    public void setParent(Node parent) { this.parent = parent; }

	    @Override
	    public String toString() {
	        return "Node{id='" + id.substring(0, 4) + "...' , nombre='" + nombre + "', tipo='" + tipo + "', hijos=" + children.size() + '}';
	    }
}