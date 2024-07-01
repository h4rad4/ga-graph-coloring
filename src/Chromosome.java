import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
    private int[] genes;
    private Graph graph;

    public Chromosome(Graph graph) {
        this.graph = graph;
        this.genes = new int[graph.getNumVertices()];
        Random random = new Random();
        for (int i = 0; i < genes.length; i++) {
            genes[i] = random.nextInt(graph.getNumColors());
        }
    }


    public int[] getGenes() {
        return genes;
    }


    public Graph getGraph() {
        return graph;
    }


    public int getFitness() {
        return graph.getNumConflicts(genes);
    }


    @Override
    public int compareTo(Chromosome other) {
        return Integer.compare(this.getFitness(), other.getFitness());
    }
}
