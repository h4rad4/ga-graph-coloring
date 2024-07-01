import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int numVertices;
    private int numColors;
    private boolean[][] adjMatrix;
    private String[] vertexNames;


    public Graph(String filePath) {
        initializeGraph(filePath);
    }


    private void initializeGraph(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<String> vertices = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("p")) {
                    String[] parts = line.split(" ");
                    numVertices = Integer.parseInt(parts[2]);
                    adjMatrix = new boolean[numVertices][numVertices];
                } else if (line.startsWith("e")) {
                    String[] parts = line.split(" ");
                    int v1 = Integer.parseInt(parts[1]) - 1;
                    int v2 = Integer.parseInt(parts[2]) - 1;
                    adjMatrix[v1][v2] = true;
                    adjMatrix[v2][v1] = true;
                    if (!vertices.contains(parts[1])) vertices.add(parts[1]);
                    if (!vertices.contains(parts[2])) vertices.add(parts[2]);
                }
            }
            vertexNames = vertices.toArray(new String[0]);
            numColors = (int) Math.ceil(Math.log(numVertices) / Math.log(2)); // aproximação inicial
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getNumVertices() {
        return numVertices;
    }


    public int getNumColors() {
        return numColors;
    }


    public boolean hasConflict(int vertex, int[] genes) {
        for (int i = 0; i < numVertices; i++) {
            if (adjMatrix[vertex][i] && genes[vertex] == genes[i]) {
                return true;
            }
        }
        return false;
    }


    public List<Integer> getValidColors(int vertex, int[] genes) {
        List<Integer> validColors = new ArrayList<>();
        boolean[] adjacentColors = new boolean[numColors];
        for (int i = 0; i < numVertices; i++) {
            if (adjMatrix[vertex][i]) {
                adjacentColors[genes[i]] = true;
            }
        }
        for (int i = 0; i < numColors; i++) {
            if (!adjacentColors[i]) {
                validColors.add(i);
            }
        }
        return validColors;
    }


    public int getNumConflicts(int[] genes) {
        int conflicts = 0;
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                if (adjMatrix[i][j] && genes[i] == genes[j]) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }


    public String[] getVertexNames() {
        return vertexNames;
    }


    public boolean[][] getAdjMatrix() {
        return adjMatrix;
    }
}
