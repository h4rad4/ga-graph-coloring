import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    public static final int POPULATION_SIZE = 50;
    static final int MAX_GENERATIONS = 20000;
    private static final double MUTATION_RATE = 0.7;


    public static void main(String[] args) {
        Graph graph = new Graph("res/data/myciel4.txt");

        List<Chromosome> population = initializePopulation(graph, POPULATION_SIZE);

        int generation = 0;
        while (generation < MAX_GENERATIONS && bestFitness(population) > 0) {
            runGA(graph, population);
            generation++;
        }

        if (bestFitness(population) > 0) {
            wisdomOfArtificialCrowds(graph, population);
        }

        Chromosome bestSolution = Collections.min(population);
        System.out.println("Melhor solução encontrada com fitness " + bestSolution.getFitness());
        for (int color : bestSolution.getGenes()) {
            System.out.print(color + " ");
        }
    }


    static List<Chromosome> initializePopulation(Graph graph, int populationSize) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosome(graph));
        }
        return population;
    }


    static void runGA(Graph graph, List<Chromosome> population) {
        List<Chromosome> parents = getParents(population);
        Chromosome child = crossover(parents.get(0), parents.get(1));
        child = mutate(child, graph);
        population.add(child);
        removeLeastFit(population);
    }


    private static void removeLeastFit(List<Chromosome> population) {
        Collections.sort(population);
        while (population.size() > POPULATION_SIZE) {
            population.remove(population.size() - 1);
        }
    }


    static int bestFitness(List<Chromosome> population) {
        return Collections.min(population).getFitness();
    }


    private static List<Chromosome> getParents(List<Chromosome> population) {
            return parentSelection1(population);
    }


    private static List<Chromosome> parentSelection1(List<Chromosome> population) {
        Random random = new Random();
        Chromosome parent1 = tournamentSelection(population, random);
        Chromosome parent2 = tournamentSelection(population, random);
        List<Chromosome> parents = new ArrayList<>();
        parents.add(parent1);
        parents.add(parent2);
        return parents;
    }


    private static Chromosome tournamentSelection(List<Chromosome> population, Random random) {
        Chromosome candidate1 = population.get(random.nextInt(population.size()));
        Chromosome candidate2 = population.get(random.nextInt(population.size()));
        return candidate1.getFitness() < candidate2.getFitness() ? candidate1 : candidate2;
    }


    private static List<Chromosome> parentSelection2(List<Chromosome> population) {
        List<Chromosome> parents = new ArrayList<>();
        Chromosome best = population.get(0);
        parents.add(best);
        parents.add(best);
        return parents;
    }


    private static Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        Random random = new Random();
        int crosspoint = random.nextInt(parent1.getGenes().length);
        Chromosome child = new Chromosome(parent1.getGraph());
        for (int i = 0; i < crosspoint; i++) {
            child.getGenes()[i] = parent1.getGenes()[i];
        }
        for (int i = crosspoint; i < parent2.getGenes().length; i++) {
            child.getGenes()[i] = parent2.getGenes()[i];
        }
        return child;
    }


    private static Chromosome mutate(Chromosome chromosome, Graph graph) {
        Random random = new Random();
        if (random.nextDouble() < MUTATION_RATE) {
            return mutation1(chromosome, graph);
        }
        return chromosome;
    }


    private static Chromosome mutation1(Chromosome chromosome, Graph graph) {
        Random random = new Random();
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (random.nextDouble() < MUTATION_RATE && graph.hasConflict(i, chromosome.getGenes())) {
                List<Integer> validColors = graph.getValidColors(i, chromosome.getGenes());
                if (!validColors.isEmpty()) {
                    chromosome.getGenes()[i] = validColors.get(random.nextInt(validColors.size()));
                } else {
                    chromosome.getGenes()[i] = random.nextInt(graph.getNumColors());
                }
            }
        }
        return chromosome;
    }


    private static Chromosome mutation2(Chromosome chromosome, Graph graph) {
        Random random = new Random();
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (random.nextDouble() < MUTATION_RATE && graph.hasConflict(i, chromosome.getGenes())) {
                chromosome.getGenes()[i] = random.nextInt(graph.getNumColors());
            }
        }
        return chromosome;
    }


    static void wisdomOfArtificialCrowds(Graph graph, List<Chromosome> population) {
        List<Chromosome> experts = getBestHalf(population);
        Chromosome aggregate = experts.get(0);

        for (int i = 0; i < aggregate.getGenes().length; i++) {
            if (graph.hasConflict(i, aggregate.getGenes())) {
                aggregate.getGenes()[i] = mostCommonColor(experts, i);
            }
        }
    }


    private static List<Chromosome> getBestHalf(List<Chromosome> population) {
        population.sort((c1, c2) -> c1.getFitness() - c2.getFitness());
        return population.subList(0, population.size() / 2);
    }


    private static int mostCommonColor(List<Chromosome> experts, int vertex) {
        int[] colorCount = new int[experts.get(0).getGraph().getNumColors()];
        for (Chromosome expert : experts) {
            colorCount[expert.getGenes()[vertex]]++;
        }
        int maxCount = -1;
        int mostCommon = -1;
        for (int i = 0; i < colorCount.length; i++) {
            if (colorCount[i] > maxCount) {
                maxCount = colorCount[i];
                mostCommon = i;
            }
        }
        return mostCommon;
    }
}
