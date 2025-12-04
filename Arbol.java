// Archivo: Arbol.java

public class Arbol {

    private Node root; // Aquí se hace referencia a la clase Node

    public Arbol(String rootName) {
        // Creamos el nodo raíz usando la clase Node
        this.root = new Node(rootName, "carpeta", null, null);
        System.out.println("Árbol creado con raíz: " + this.root.getNombre());
    }

    // Método de ejemplo para añadir un nodo
    public Node findFolder(String folderName, Node currentNode) {
        if (currentNode == null) {
            return null;
        }
        if (currentNode.getNombre().equals(folderName) && currentNode.getTipo().equals("carpeta")) {
            return currentNode;
        }
        for (Node child : currentNode.getChildren()) {
            Node result = findFolder(folderName, child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    //funcion para añadir nodos hijos que usa como parametros el nombre del padre, nombre del hijo, tipo de hijo y contenido
    public void addChildren(String parent, String child, String childtype, String content) {
        //creamos un nodo folder buscando el padre
        Node folder = findFolder(parent, this.root);
        if (folder != null) { //si existe el padre verificamos si es del tipo carpeta
            if("carpeta".equals(folder.getTipo())) {
                //si es el caso creamos el nuevo nodo hijo
                Node newNode = new Node(child, childtype, content, folder);
                folder.getChildren().add(newNode);
                System.out.println("Nodo añadido: " + newNode.getNombre() + " bajo la carpeta: " + folder.getNombre());
            } else {
                System.out.println("Tipo de nodo inválido: " + childtype);
            }
        } else {
            System.out.println("Carpeta no encontrada: " + parent);
        }
    }

    public void displayTree() {
        System.out.println("\n--- Estructura Completa del Árbol ---");
        // Comienza la visualización desde el nodo raíz con sangría inicial ""
        identar(this.root, "");
    }

   
    private void identar(Node node, String indent) {
        // 1. Imprime el nodo actual con la sangría proporcionada
        System.out.println(indent + "- " + node.getNombre() + " (" + node.getTipo() + ")");

        // 2. Recorre recursivamente los hijos del nodo actual, aumentando la sangría
        for (Node child : node.getChildren()) {
            
            identar(child, indent + "  "); 
        }
    }

    // Método main para probar la clase Arbol
    public static void main(String[] args) {
        Arbol miArbol = new Arbol("/");

        // Usamos métodos que internamente utilizan la clase Node
        miArbol.addChildren("/", "Documentos", "carpeta", null);
        miArbol.addChildren("Documentos","informe.pdf", "archivo", "Contenido del informe.");
        miArbol.addChildren("Documentos","resumen.txt", "archivo", "Contenido del informe.");
        miArbol.addChildren("/", "Imagenes", "carpeta", null);
        miArbol.addChildren("Imagenes","foto.jpg", "archivo", "Contenido de la foto.");
        miArbol.addChildren("/", "Descargas", "carpeta", null);
        miArbol.addChildren("Descargas","setup.exe", "archivo", "Contenido del ejecutable.");
        miArbol.addChildren("Descargas","manual.pdf", "archivo", "Contenido del manual PDF.");
        miArbol.addChildren("/", "Musica", "carpeta", null);
        miArbol.addChildren("Musica","cancion.mp3", "archivo", "Contenido de la canción.");
        miArbol.displayTree();
    }
}

