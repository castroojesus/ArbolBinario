import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {
    
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // insertar nombre y asociar el ID del nodo
    public void insert(String word, String nodeId) {
        TrieNode current = root;
        String processedWord = word.toLowerCase(); 

        for (char ch : processedWord.toCharArray()) {
            current.getChildren().putIfAbsent(ch, new TrieNode());
            current = current.getChildren().get(ch);
        }
        current.setEndOfWord(true);
        current.setNodeId(nodeId); 
    }

    // autocompletado, devulve una lista de nombres que empiezan igual 
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode current = root;
        String processedPrefix = prefix.toLowerCase();

        for (char ch : processedPrefix.toCharArray()) {
            if (!current.getChildren().containsKey(ch)) {
                return results;
            }
            current = current.getChildren().get(ch);
        }

        findAllWords(current, prefix, results);
        return results;
    }

    // funcion para encontrar todas las palabras desde un nodo 
    private void findAllWords(TrieNode node, String currentWord, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(currentWord + " [ID: " + node.getNodeId().substring(0, 4) + "...]"); 
        }

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            findAllWords(entry.getValue(), currentWord + entry.getKey(), results);
        }
    }
}