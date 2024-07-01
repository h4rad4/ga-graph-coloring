import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph("res/data/homer.txt");

        List<Chromosome> population = GeneticAlgorithm.initializePopulation(graph, GeneticAlgorithm.POPULATION_SIZE);

        int generation = 0;
        while (generation < GeneticAlgorithm.MAX_GENERATIONS && GeneticAlgorithm.bestFitness(population) > 0) {
            GeneticAlgorithm.runGA(graph, population);
            generation++;
        }

        if (GeneticAlgorithm.bestFitness(population) > 0) {
            GeneticAlgorithm.wisdomOfArtificialCrowds(graph, population);
        }

        Chromosome bestSolution = Collections.min(population);
        System.out.println("Melhor solução encontrada com  fitness " + bestSolution.getFitness());

        for (int color : bestSolution.getGenes()) {
            System.out.print(color + " ");
        }

        SparseMultigraph<String, String> visualGraph = new SparseMultigraph<>();
        String[] vertexNames = graph.getVertexNames();
        boolean[][] adjMatrix = graph.getAdjMatrix();
        for (int i = 0; i < vertexNames.length; i++) {
            visualGraph.addVertex(vertexNames[i]);
        }
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = i + 1; j < adjMatrix[i].length; j++) {
                if (adjMatrix[i][j]) {
                    visualGraph.addEdge("Edge-" + i + "-" + j, vertexNames[i], vertexNames[j]);
                }
            }
        }

        FRLayout<String, String> layout = new FRLayout<>(visualGraph);
        layout.setSize(new Dimension(600, 600));

        BasicVisualizationServer<String, String> vv = new BasicVisualizationServer<>(layout);
        vv.setPreferredSize(new Dimension(650, 650));

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> {
            int index = java.util.Arrays.asList(vertexNames).indexOf(vertex);
            int colorIndex = bestSolution.getGenes()[index];
            return Color.getHSBColor(colorIndex * 1.0f / graph.getNumColors(), 1.0f, 1.0f);
        });

        JFrame frame = new JFrame("Grafo Colorido");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
