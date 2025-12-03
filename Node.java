import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Clase para el modelo de nodo en el Árbol General.
public class Node {

    private String id;          // identificador 
    private String nombre;      // nombre del archivo o carpeta
    private String tipo;        // 'carpeta' o 'archivo'
    private String contenido;   // contenido para archivos
    private List<Node> children; // lista de nodos hijos

    private Node parent;

    public Node(String nombre, String tipo, String contenido, Node parent) {
        // genera un ID 
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.tipo = tipo.toLowerCase();
        this.parent = parent;
        this.children = new ArrayList<>();

        //contemido solo para archivos
        if ("archivo".equals(this.tipo)) {
            this.contenido = contenido;
        } else {
            this.contenido = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id.substring(0, 4) + "...' " +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", hijos=" + children.size() +
                '}';
    }


   //preubas para la class node el mvp (minimo producto viable)
    public static void main(String[] args) {
        // crear la Raíz
        Node root = new Node("/", "carpeta", null, null);

        // crear una carpeta 'Documentos' bajo la raíz
        Node docs = new Node("Documentos", "carpeta", null, root);
        root.getChildren().add(docs);

        // crear un archivo 'apunte.txt' bajo 'Documentos'
        Node file = new Node("apunte.txt", "archivo", "Contenido del archivo de texto.", docs);
        docs.getChildren().add(file);

        System.out.println("Nodo Raíz: " + root);
        System.out.println("Hijo de la Raíz: " + root.getChildren().get(0).getNombre());
        System.out.println("Tipo del Archivo: " + file.getTipo());
    }
}