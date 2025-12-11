package ProyectoArboles;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    
    private Map<Character, TrieNode> children; 
    private boolean isEndOfWord; 
    private String nodeId; // ID del nodo del Arbol.java asociado a la palabra

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        nodeId = null;
    }
    
    // getters y setters

    public Map<Character, TrieNode> getChildren() { return children; }
    public boolean isEndOfWord() { return isEndOfWord; }
    public void setEndOfWord(boolean endOfWord) { isEndOfWord = endOfWord; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}