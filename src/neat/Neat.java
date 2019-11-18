package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*Main control class for NEAT algorithm*/
enum MutationAction
{
	WEIGHT,
	CONNECTION,
	NODE
}
public abstract class Neat {
	

	private int globalInovationNumber;
	/*inNode and outNode to Gene mapping for all existing genes*/
	private HashMap<Pair<Node, Node>, Gene> allExistingGenes;
	private ArrayList<Node> inputNodes;
	private ArrayList<Node> outputNodes;
	private int initPopulationCount;
	private ArrayList<Species> speciesList;
	
	public Neat(int inputNodes, int outputNodes, int initPopulationCount)
	{
		this.allExistingGenes = new HashMap<Pair<Node, Node>, Gene>();
		this.globalInovationNumber = 1;
		this.inputNodes = new ArrayList<Node>();
		this.outputNodes = new ArrayList<Node>();
		this.initPopulationCount = initPopulationCount;
		this.speciesList = new ArrayList<Species>();
		initPopulation(inputNodes, outputNodes, initPopulationCount);
	}
	private void initPopulation(int inputCount, int outputCount, int initPopulationCount)
	{
		
	}
	public void setSpeciationParameters(double deltaThreshold, double c1, double c2, double c3)
	{
		Globals.delta_Threshhold = deltaThreshold;
		Globals.delta_C1 = c1;
		Globals.delta_C2 = c2;
		Globals.delta_C3 = c3;
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
		NodeType type;
		RandomGenerator randomGenerator = new RandomGenerator();
		Node[] nodes = (Node[]) genome.getNodes().values().toArray();
		double weight = randomGenerator.getRandomSignedDouble();
		int innovationNumber, count = 0;
		while(true)
		{
			type = NodeType.INPUT;
			while(type == NodeType.INPUT)
			{
				inNode = (Node) randomGenerator.getRandomAction(nodes);
				type = inNode.getNodeType();
			}
			type = NodeType.OUTPUT;
			int id = inNode.getNodeId();
			while(type == NodeType.OUTPUT || id == inNode.getNodeId())
			{
				outNode = (Node) randomGenerator.getRandomAction(nodes);
				type = outNode.getNodeType();
				id = outNode.getNodeId();
			}
			if(!genome.containsGene(inNode, outNode))
				break;
			if(count > 10)
				return false;
			count++;
		}
		Pair<Node, Node> pair = new Pair<Node, Node>(inNode, outNode);
		if(allExistingGenes.containsKey(pair))
			innovationNumber = allExistingGenes.get(pair).getInovationNumber();
		else
			innovationNumber = globalInovationNumber++;
		Gene newGene = new Gene(inNode, outNode, weight, true, innovationNumber);
		genome.addGene(newGene);
		return true;
	}
	
	private void addRandomNodeToGenome(Genome genome)
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		int newInnovationNumber1, newInnovationNumber2;
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
			newInnovationNumber1 = this.globalInovationNumber++;
		if(allExistingGenes.containsKey(pair2))
			newInnovationNumber2 = allExistingGenes.get(pair2).getInovationNumber();
		else
			newInnovationNumber2 = this.globalInovationNumber++;
		newGene1 = new Gene(randomGene.getInNode(), newNode, randomGene.getWeight(), true, newInnovationNumber1);
		newGene2 = new Gene(newNode, randomGene.getOutNode(), 1, true, newInnovationNumber2);
		genome.addGene(newGene1);
		genome.addGene(newGene2);
	}
	
	private double min(double a, double b)
	{
		return a > b ? b : a;
	}
	private double max(double a, double b)
	{
		return a > b ? a : b;
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
			int delta = (int) randomGenerator.getRandomAction(action);
			delta *= Globals.weightDelta;
			weight = randomGene.getWeight() + delta;
			weight = min(max(weight, Globals.minWeight), Globals.maxWeight);
		}
		randomGene.setWeight(weight);
	}
	
	public void mutateGenome(Genome genome)
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		double[] mutationProbs = {Globals.weightMutationProbability, Globals.connectionMutationProbability, Globals.nodeMutationProbability};
		MutationAction[] possibleActions = {MutationAction.WEIGHT, MutationAction.CONNECTION, MutationAction.NODE};
		MutationAction action;
		action = (MutationAction) randomGenerator.probablityBasedAction(possibleActions, mutationProbs);
		switch(action)
		{
		case WEIGHT:
			mutateWeights(genome);
			break;
		case CONNECTION:
			addRandomConnectionToGenome(genome);
			break;
		case NODE:
			addRandomNodeToGenome(genome);
			break;
		}
	}
	
	public void selection()
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
				try
				{
					s.removeFromIndexToEnd(population + 1);
				}
				catch(IndexOutOfBoundsException e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	public abstract void calculateFitnessScore();
}
