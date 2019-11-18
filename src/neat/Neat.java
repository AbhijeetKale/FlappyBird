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
		this.inputNodes = new ArrayList<Node>(inputNodes);
		this.outputNodes = new ArrayList<Node>(outputNodes);
		this.initPopulationCount = initPopulationCount;
		this.speciesList = new ArrayList<Species>();
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
	
	private void addRandomConnectionToGenome(Genome genome)
	{

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
		Gene gene = genome.getGene(randomGenomeIndex);
		double weight1, weight2;
		
		weight1 = randomGenerator.getRandomSignedDouble();
		weight2 = randomGenerator.getRandomSignedDouble();
		gene.setEnabledFlag(false);
		pair1 = new Pair<Node, Node>(gene.getInNode(), newNode);
		pair2 = new Pair<Node, Node>(newNode, gene.getOutNode());
		if(allExistingGenes.containsKey(pair1))
			newInnovationNumber1 = allExistingGenes.get(pair1).getInovationNumber();
		else
			newInnovationNumber1 = this.globalInovationNumber++;
		if(allExistingGenes.containsKey(pair2))
			newInnovationNumber2 = allExistingGenes.get(pair2).getInovationNumber();
		else
			newInnovationNumber2 = this.globalInovationNumber++;
		newGene1 = new Gene(gene.getInNode(), newNode, weight1, true, newInnovationNumber1);
		newGene2 = new Gene(newNode, gene.getOutNode(), weight2, true, newInnovationNumber2);
		genome.addGene(newGene1);
		genome.addGene(newGene2);
	}
	
	private void mutateWeights(Genome genome)
	{
		
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
