package neat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*Main control class for NEAT algorithm*/
enum MutationAction
{
	WEIGHT,
	CONNECTION,
	NODE
}
public abstract class Neat {

	private int globalInovationNumber;
	/*inNode (Node connection is going to) and outNode (Node connection is coming out of) to Gene mapping for all existing genes*/
	private HashMap<Pair<Node, Node>, Gene> allExistingGenes;
	private ArrayList<Species> speciesList;
	private int inputNodeCount;
	private int outputNodeCount;

	public Neat(int inputNodes, int outputNodes, int initPopulationCount)
	{
		this.allExistingGenes = new HashMap<Pair<Node, Node>, Gene>();
		this.globalInovationNumber = 1;
		this.speciesList = new ArrayList<Species>();
		this.inputNodeCount = inputNodes;
		this.outputNodeCount = outputNodes;
		initPopulation(inputNodes, outputNodes, initPopulationCount);
	}
	public void printGenome(Genome genome)
	{
		Gene testGene;
		SortedListIterator<Gene> geneIterator = genome.iterator();
		System.out.println("Genome" + ": Fitness = " + genome.getFitnessScore());
		while(geneIterator.hasNext())
		{
			testGene = geneIterator.next();
			System.out.println(testGene.toString());
		}
		System.out.println();
	}
	public void printAllSpecies()
	{
		Iterator<Species> speciesIterator = speciesList.iterator();
		/*Testing */
		Genome test;
		int count = 1, counter;
		while(speciesIterator.hasNext())
		{
			System.out.println("Species" + count + ":");
			Species species1 = speciesIterator.next();
			SortedListIterator<Genome> genomeIterator = species1.iterator();
			counter = 1;
			while(genomeIterator.hasNext())
			{
				test = genomeIterator.next();
				System.out.print(counter + ") ");
				printGenome(test);
				counter++;
			}
			count++;
		}
	}
	public void testing()
	{

	}
	private void initPopulation(int inputCount, int outputCount, int initPopulationCount)
	{
		Species species1 = new Species();
		Genome genome;
		Gene gene;
		int maxConnections = inputCount * outputCount;
		boolean newInnovationNumber = false;
		double weight;
		int innovationNumber;
		Node tmp;
		RandomGenerator randomGenerator = new RandomGenerator();
		Node[] outputNodes, inputNodes;
		inputNodes = new Node[inputCount];
		outputNodes = new Node[outputCount];
		for(int count = 0; count < initPopulationCount; count++)
		{
			genome = new Genome();
			for(int counter = 0; counter < inputCount; counter++)
			{
				tmp = new Node(genome.getNodes().size(), NodeType.INPUT);
				genome.addNode(tmp);
				inputNodes[counter] = tmp;
			}
			for(int counter = 0; counter < outputCount; counter++)
			{
				tmp = new Node(genome.getNodes().size(), NodeType.OUTPUT);
				genome.addNode(tmp);
				outputNodes[counter] = tmp;
			}
			Node inputNode = null, outputNode = null;
			int connCount = 0;
			int rand = max(1, randomGenerator.getRandomIntWithLimit(maxConnections));
			while(connCount < rand)
			{
				newInnovationNumber = false;
				inputNode = (Node) randomGenerator.getRandomAction(inputNodes);
				outputNode = (Node) randomGenerator.getRandomAction(outputNodes);
				if(genome.containsGene(outputNode, inputNode))
					continue;
				connCount++;
				weight = randomGenerator.getRandomSignedDouble();
				Pair<Node, Node> p = new Pair<Node, Node>(outputNode, inputNode);
				if(allExistingGenes.containsKey(p))
					innovationNumber = allExistingGenes.get(p).getInovationNumber();
				else
				{
					innovationNumber = this.globalInovationNumber++;
					newInnovationNumber = true;
				}
				gene = new Gene(outputNode, inputNode, weight, true, innovationNumber);
				genome.addGene(gene);
				if(newInnovationNumber)
					allExistingGenes.put(p, gene);
			}
			genome.setFitnessScore(this.calculateFitnessScore(genome));
			species1.addGenome(genome);
		}
		speciesList.add(species1);
	}
	public void setSpeciationParameters(double deltaThreshold, double c1, double c2, double c3)
	{
		Globals.delta_Threshhold = deltaThreshold;
		Globals.delta_C1 = c1;
		Globals.delta_C2 = c2;
		Globals.delta_C3 = c3;
	}

	public void simulateGeneration()
	{
		/*Speciation*/
		/*Selection*/
		/*CrossOver*/
		/*Mutation*/
		/*Fitness Calculation*/
	}
	private static double sigmoid(double x)
	{
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}
	
	public void setSelectionParameters(double populationSurvivalPercentage, int minimumPopulation)
	{
		Globals.populationSruvivalPercentage = populationSurvivalPercentage;
		Globals.minimumPopulation = minimumPopulation;
	}
	
	public void setMutaionParameters(double weightMutationProbability, double connectionMutationProbability,
									 double nodeMutationProbability, double randomlyChangeWeightProbability,
									 double weightDelta)
	{
		Globals.weightMutationProbability = weightMutationProbability;
		Globals.connectionMutationProbability = connectionMutationProbability;
		Globals.nodeMutationProbability = nodeMutationProbability;
		Globals.randomlyChangeWeightProbability = randomlyChangeWeightProbability;
		Globals.weightDelta = weightDelta;
	}

	public void setConnectionParams(double minWeight, double maxWeight)
	{
		Globals.minWeight = minWeight;
		Globals.maxWeight = maxWeight;
	}

	private boolean addRandomConnectionToGenome(Genome genome)
	{
		Node inNode = null, outNode = null;
		RandomGenerator randomGenerator = new RandomGenerator();
		ArrayList<Node> inNodes = new ArrayList<Node>();
		ArrayList<Node> outNodes = new ArrayList<Node>();
		double weight = randomGenerator.getRandomSignedDouble();
		int innovationNumber, count = 0;
		boolean newInnovationNumber = false;
		Collection<Node> nodes =  genome.getNodes().values();
		Iterator<Node> nodeIterator = nodes.iterator();
		while(nodeIterator.hasNext())
		{
			Node tmp = nodeIterator.next();
			if(tmp.getNodeType() == NodeType.INPUT)
				outNodes.add(tmp);
			else if(tmp.getNodeType() == NodeType.OUTPUT)
				inNodes.add(tmp);
			else if(tmp.getNodeType() == NodeType.HIDDEN)
			{
				inNodes.add(tmp);
				outNodes.add(tmp);
			}
		}
		while(true)
		{
			inNode = (Node) randomGenerator.getRandomAction(inNodes);
			outNode = (Node) randomGenerator.getRandomAction(outNodes);
			if((inNode.getNodeType() != outNode.getNodeType()) 
				&& (!genome.containsGene(inNode, outNode)))
				break;
			if(count > 10)
				return false;
			count++;
		}
		Pair<Node, Node> pair = new Pair<Node, Node>(inNode, outNode);
		if(allExistingGenes.containsKey(pair))
			innovationNumber = allExistingGenes.get(pair).getInovationNumber();
		else
		{
			innovationNumber = globalInovationNumber++;
			newInnovationNumber = true;
		}
		Gene newGene = new Gene(inNode, outNode, weight, true, innovationNumber);
		genome.addGene(newGene);
		if(newInnovationNumber)
			allExistingGenes.put(new Pair<Node, Node>(inNode, outNode), newGene);
		return true;
	}
	/*Randomly selecting a genome and adding node between 2 nodes*/
	private void addRandomNodeToGenome(Genome genome)
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		int newInnovationNumber1, newInnovationNumber2;
		int newGeneFlag = 0;
		Pair<Node, Node> pair1, pair2;
		int nodeNo = genome.getNodes().size();
		Node newNode = new Node(nodeNo, NodeType.HIDDEN);
		int randomGenomeIndex = randomGenerator.getRandomIntWithLimit(genome.genomeSize());
		Gene newGene1, newGene2;
		Gene randomGene = genome.getGene(randomGenomeIndex);
		
		randomGene.setEnabledFlag(false);
		pair1 = new Pair<Node, Node>(randomGene.getInNode(), newNode);
		pair2 = new Pair<Node, Node>(newNode, randomGene.getOutNode());
		if(allExistingGenes.containsKey(pair1))
			newInnovationNumber1 = allExistingGenes.get(pair1).getInovationNumber();
		else
		{
			newInnovationNumber1 = this.globalInovationNumber++;
			newGeneFlag |= 1;
		}
		if(allExistingGenes.containsKey(pair2))
			newInnovationNumber2 = allExistingGenes.get(pair2).getInovationNumber();
		else
		{
			newInnovationNumber2 = this.globalInovationNumber++;
			newGeneFlag |= 1 << 1;
		}
		newGene1 = new Gene(randomGene.getInNode(), newNode, randomGene.getWeight(), true, newInnovationNumber1);
		newGene2 = new Gene(newNode, randomGene.getOutNode(), 1, true, newInnovationNumber2);
		genome.addGene(newGene1);
		genome.addGene(newGene2);
		if((newGeneFlag & 1) == 1)
			allExistingGenes.put(pair1, newGene1);
		if((newGeneFlag >> 1) == 1)
			allExistingGenes.put(pair2, newGene2);
	}

	/*Randomly selecting a gene and mutating it's weights (either randomly or incrementally)*/
	private void mutateWeights(Genome genome)
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		Boolean[] actions = {true, false};
		double weight;
		double probs[] = {Globals.randomlyChangeWeightProbability, 100 - Globals.randomlyChangeWeightProbability};
		boolean randomlyMutate = (boolean) randomGenerator.probablityBasedAction(actions, probs);
		int randomGenomeIndex = randomGenerator.getRandomIntWithLimit(genome.genomeSize());
		Gene randomGene = genome.getGene(randomGenomeIndex);

		if(randomlyMutate)
			weight = randomGenerator.getRandomSignedDouble();
		else
		{
			Integer[] action = {-1, 1};
			double delta = (int) randomGenerator.getRandomAction(action);
			delta *= Globals.weightDelta;
			weight = randomGene.getWeight() + delta;
			weight = min(max(weight, Globals.minWeight), Globals.maxWeight);
		}
		randomGene.setWeight(weight);
	}

	private void mutateGenome(Genome genome)
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		double[] mutationProbs = {Globals.weightMutationProbability, Globals.connectionMutationProbability, Globals.nodeMutationProbability};
		MutationAction[] possibleActions = {MutationAction.WEIGHT, MutationAction.CONNECTION, MutationAction.NODE};
		MutationAction action;
		action = (MutationAction) randomGenerator.probablityBasedAction(possibleActions, mutationProbs);
		switch(action)
		{
		case NODE:
			addRandomNodeToGenome(genome);
			break;
		case CONNECTION:
			if(addRandomConnectionToGenome(genome))
				break;
		case WEIGHT:
			mutateWeights(genome);
			break;
		}
	}
	
	private void selection()
	{
		Iterator<Species> i = speciesList.iterator();
		Species s;
		int population;
		while(i.hasNext())
		{
			s = i.next();
			population = s.getSpeciesPopulation();
			if(population > Globals.minimumPopulation)
			{
				population = (int) (population * Globals.populationSruvivalPercentage) / 100;
				population = min(Globals.minimumPopulation, population);
				try
				{
					s.removeFromIndexToEnd(population);
				}
				catch(IndexOutOfBoundsException e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	private int min(int a, int b)
	{
		return a < b ? a : b;
	}
	private int max(int a, int b)
	{
		return a > b ? a : b;
	}
	private double min(double a, double b)
	{
		return a > b ? b : a;
	}
	private double max(double a, double b)
	{
		return a > b ? a : b;
	}
	public abstract double calculateFitnessScore(Genome genome);
}
