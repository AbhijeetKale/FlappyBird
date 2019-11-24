package neat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

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
		System.out.println("Genome" + ": label = " + genome.getLabel() + " Fitness = " + genome.getFitnessScore());
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
			genome.setLabel(GenomeLabel.INIT);
			species1.addGenome(genome);
			printGenome(genome);
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
		RandomGenerator randomGenerator = new RandomGenerator();
		Iterator<Species> iSpecies = speciesList.iterator();
		int populationPreSelection;
		ArrayList<Genome> newGeneration = new ArrayList<Genome>();
		System.out.println("***********************************************");
		printAllSpecies();
		System.out.println("***********************************************");
		while(iSpecies.hasNext())
		{
			Species species = iSpecies.next();
			populationPreSelection = species.getSpeciesPopulation();
			//selection
			this.selection(species);
			//selection
			//CrossOver
			int top50 = 1 + species.getSpeciesPopulation() / 2;
			if(species.getSpeciesPopulation() > 2)
			{
				int crossOVerCount = (int) ((populationPreSelection - species.getSpeciesPopulation())
											 * Globals.matingCrossOverProportin / 100);
				for(int count = 0; count < crossOVerCount; count++)
				{
					int parentIdx1 = randomGenerator.getRandomIntWithLimit(top50);
					int parentIdx2 = parentIdx1;
					while(parentIdx2 == parentIdx1)
						parentIdx2 = randomGenerator.getRandomIntWithLimit(top50);
					Genome parent1 = species.getGenome(parentIdx1);
					Genome parent2 = species.getGenome(parentIdx2);
					Genome child = Genome.crossOver(parent1, parent2, 
													this.inputNodeCount, this.outputNodeCount);
					child.setLabel(GenomeLabel.CROSSOVER);
					newGeneration.add(child);
				}
			}
			//CrossOver
			//mutation
			int mutationCount = populationPreSelection - species.getSpeciesPopulation();
			for(int count = 0; count < mutationCount; count++)
			{
				int childIdx = randomGenerator.getRandomIntWithLimit(species.getSpeciesPopulation());
				try
				{
					Genome child = (Genome) species.getGenome(childIdx).clone();
					mutateGenome(child);
					newGeneration.add(child);
				}
				catch(CloneNotSupportedException e)
				{
					e.printStackTrace();
				}
				catch(IndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			//mutation
			
			SortedListIterator<Genome> iAncestors = species.iterator();
			while(iAncestors.hasNext())
				newGeneration.add(iAncestors.next());
		}
		for(int count = speciesList.size() - 1; count >= 0; count--)
			speciesList.remove(count);
		//speciation
		boolean newSpeciesIdentified = true;
		Iterator<Genome> iNewGenome;
		iSpecies = speciesList.iterator();
		iNewGenome = newGeneration.iterator();
		while(iNewGenome.hasNext())
		{
			newSpeciesIdentified = true;
			Genome genome = iNewGenome.next();
			printGenome(genome);
			genome.setFitnessScore(calculateFitnessScore(genome));
			iSpecies = speciesList.iterator();
			while(iSpecies.hasNext())
			{
				Species species = iSpecies.next();
				if(species.genomeBelongsToSpecies(genome))
				{
					species.addGenome(genome);
					newSpeciesIdentified = false;
					break;
				}
			}
			if(newSpeciesIdentified)
			{
				Species species = new Species();
				speciesList.add(species);
				species.addGenome(genome);
			}
		}
		//speciation
	}
	private static double sigmoid(double x)
	{
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}

	public void setActivationValue(double[] activationValue, 
									ArrayList<listNode<Node>> nodeDependencyGraph, int nodeId
									,HashMap<Pair<Integer, Integer>, Double> weightMap)
									throws InCorrectInputException
	{
		double returnValue = Globals.nodeActivationUnset;
		listNode<Node> data = nodeDependencyGraph.get(nodeId);
		listNode<Node> tmp = data.next;
		if(tmp == null && activationValue[nodeId] == Globals.nodeActivationUnset)
		{
			activationValue[nodeId] = 0;
			throw new InCorrectInputException("No input conns to Current Node: " + nodeId);
		}
		else
		{
			returnValue = 0;
			while(tmp != null)
			{
				Node node = tmp.data;
				if(activationValue[node.getNodeId()] == Globals.nodeMutationProbability)
					setActivationValue(activationValue, nodeDependencyGraph, node.getNodeId(), weightMap);
				double weight = (double) weightMap.get(new Pair<Integer, Integer>(nodeId, node.getNodeId()));
				returnValue += weight * activationValue[node.getNodeId()];
				tmp = tmp.next;
			}
		}
		activationValue[nodeId] = sigmoid(returnValue);
	}
	
	public List<Double> calculateOutputForGenome(Genome genome, double[] input) throws InCorrectInputException
	{
		if(input.length != this.inputNodeCount)
			throw new InCorrectInputException("Incorrect number of inputs");
		List<Double> outputNodes = new ArrayList<Double>();
		ArrayList<listNode<Node>> nodeDependencyGraph;
		HashMap<Pair<Integer, Integer>, Double> weightMap;
		double[] activationValue = new double[genome.getNodes().size()];
		nodeDependencyGraph = genome.nodeDependencyGraph();
		weightMap = genome.genomeWeightMap();
		for(int count = 0; count < activationValue.length; count++)
			activationValue[count] = Globals.nodeActivationUnset;
		for(int count = 0; count < this.inputNodeCount; count++)
			activationValue[count] = input[count];
		int iSize = this.outputNodeCount + this.inputNodeCount;
		for(int count = this.inputNodeCount; count < iSize; count++)
		{
			if(activationValue[count] == Globals.nodeActivationUnset)
			{
				try
				{
					setActivationValue(activationValue, nodeDependencyGraph, count, weightMap);
				}
				catch(InCorrectInputException e)
				{
					e.printStackTrace();
				}

			}
			outputNodes.add(activationValue[count]);
		}
		return outputNodes;
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
		genome.mutateRandomWeight();
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
			genome.setLabel(GenomeLabel.NODE_MUTATED);
			break;
		case CONNECTION:
			if(addRandomConnectionToGenome(genome))
			{
				genome.setLabel(GenomeLabel.CONN_MUTATED);
				break;
			}
		case WEIGHT:
			mutateWeights(genome);
			genome.setLabel(GenomeLabel.WEIGHT_MUTATED);
			break;
		}
	}
	
	private void selection(Species s)
	{
		int population;
		population = s.getSpeciesPopulation();
		if(population > Globals.minimumPopulation)
		{
			population = (int) (population * Globals.populationSruvivalPercentage) / 100;
			population = max(Globals.minimumPopulation, population);
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
	private int max(int a, int b)
	{
		return a > b ? a : b;
	}
	public abstract double calculateFitnessScore(Genome genome);
}
