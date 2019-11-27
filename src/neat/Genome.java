package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
enum GenomeLabel
{
	UNSET,
	NODE_MUTATED,
	WEIGHT_MUTATED,
	CONN_MUTATED,
	CROSSOVER,
	PARENT,
	ANCESTOR,
	INIT
}
/*Represent the list of Genes/Connections in a Neural net, basically the neural itself*/
public class Genome implements Comparator<Gene>, Cloneable{
	
	private SortedList<Gene> genome;
	private double fitness;
	private HashMap<Node, Node> nodes;
	private ArrayList<listNode<Node>> dependencyGraph = null;
	private boolean updatedDependencyGraph = false;
	private HashMap<Pair<Integer, Integer>, Double> weightMap = null;
	private boolean updatedWeightMap = false;
	private GenomeLabel label = GenomeLabel.UNSET;
	
	public Genome()
	{
		genome = new SortedList<Gene>(this);
		nodes = new HashMap<Node, Node>();
		fitness = 0;
		this.updatedDependencyGraph = false;
		this.updatedWeightMap = false;
	}
	public void setLabel(GenomeLabel label)
	{
		this.label = label;
	}
	public GenomeLabel getLabel()
	{
		return this.label;
	}
	void setFitnessScore(double fitness)	
	{
		this.fitness = fitness;
	}
	public SortedListIterator<Gene> iterator()
	{
		return this.genome.iterator();
	}
	public void addGene(Gene g)
	{
		updatedDependencyGraph = false;
		updatedWeightMap = false;
		if(!nodes.containsKey(g.getInNode()))
			addNode(g.getInNode());
		if(!nodes.containsKey(g.getOutNode()))
			addNode(g.getOutNode());
		this.genome.add(g);
	}
	public double getFitnessScore()
	{
		return this.fitness;
	}

	public static Genome crossOver(Genome parent1, Genome parent2, int inputCount, int outputCount)
	{
		Genome child = new Genome();
		GenomePairData pairData = new GenomePairData(parent1, parent2);
		ArrayList<Pair<Gene, Gene>> matchingGenes = pairData.getMatchingGenes();
		ArrayList<Gene> disjointGenes1 = pairData.getDisjointGenes1();
		ArrayList<Gene> disjointGenes2 = pairData.getDisjointGenes2();
		ArrayList<Gene> excessGenes = null, excessGenes2 = null;
		RandomGenerator randomGenerator = new RandomGenerator();
		Node tmp;
		//adding input nodes
		for(int counter = 0; counter < inputCount; counter++)
		{
			tmp = new Node(counter, NodeType.INPUT);
			child.addNode(tmp);
		}
		//adding output nodes
		for(int counter = 0; counter < outputCount; counter++)
		{
			tmp = new Node(inputCount + counter, NodeType.OUTPUT);
			child.addNode(tmp);
		}
		if(parent1.getFitnessScore() == parent2.getFitnessScore())
		{
			excessGenes = pairData.getExcessGenes1();
			excessGenes2 = pairData.getExcessGenes2();
		}
		else if(parent1.getFitnessScore() > parent2.getFitnessScore())
		{
			excessGenes = pairData.getExcessGenes1();		
			excessGenes2 = null;
		}
		else if(parent1.getFitnessScore() < parent2.getFitnessScore())
		{
			excessGenes = pairData.getExcessGenes2();
			excessGenes2 = null;
		}
		Iterator<Pair<Gene, Gene>> i = matchingGenes.iterator();
		Pair<Gene, Gene> tmpGene;
		while(i.hasNext())
		{
			//matching gene code
			try
			{
				tmpGene = i.next();
				Gene[] geneArray = {tmpGene.getKey(), tmpGene.getValue()};
				double[] geneProbs = {50, 50};
				Gene randomMatchGene = (Gene) randomGenerator.probablityBasedAction(geneArray, geneProbs);
				Gene gene = (Gene) randomMatchGene.clone();
				if(!tmpGene.getKey().isEnabled() || !tmpGene.getValue().isEnabled())
				{
					double[] enableDisable = {100 - Globals.enableDisableFlagProbablity, Globals.enableDisableFlagProbablity};
					Boolean[] b = {true, false};
					boolean flag = (boolean) randomGenerator.probablityBasedAction( b, enableDisable);
					gene.setEnabledFlag(flag);
				}
				child.addGene(gene);
			}
			catch(CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}
		
		Iterator<Gene> gi1 = disjointGenes1.iterator();
		while(gi1.hasNext())
		{
			try
			{
				child.addGene((Gene) gi1.next().clone());
			}
			catch(CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}

		Iterator<Gene> gi2 = disjointGenes2.iterator();
		while(gi2.hasNext())
		{
			try
			{
				child.addGene((Gene) gi2.next().clone());
			}
			catch(CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}
		
		Iterator<Gene> ei1 = excessGenes.iterator();
		while(ei1.hasNext())
		{
			try
			{
				child.addGene((Gene) ei1.next().clone());
			}
			catch(CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}
		if(excessGenes2 != null)
		{
			Iterator<Gene> ei2 = excessGenes2.iterator();
			while(ei2.hasNext())
			{
				try
				{
					child.addGene((Gene) ei2.next().clone());
				}
				catch(CloneNotSupportedException e)
				{
					e.printStackTrace();
				}
			}		}
		
		return child;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Genome cloneGenome = new Genome();
		SortedList<Gene> cloneGeneList = new SortedList<Gene>(cloneGenome);
		SortedListIterator<Gene> iGene = this.genome.iterator();
		while(iGene.hasNext())
		{
			Gene cloneGene = (Gene) iGene.next().clone();
			cloneGeneList.add(cloneGene);
		}
		cloneGenome.setGeneList(cloneGeneList);
		cloneGenome.setNodeHashMap((HashMap<Node, Node>)this.nodes.clone());
		cloneGenome.setFitnessScore(this.fitness);
		cloneGenome.setLabel(this.label);
		return cloneGenome;
	}
	
	private void setNodeHashMap(HashMap<Node, Node> nodeMap)
	{
		this.nodes = nodeMap;
	}
	
	private void setGeneList(SortedList<Gene> geneList)
	{
		this.genome = geneList;
	}
	
	public int genomeSize()
	{
		return this.genome.size();
	}
	
	public Gene getGene(int index)
	{
		return this.genome.getData(index);
	}
	public void addNode(Node node)
	{
		updatedDependencyGraph = false;
		updatedWeightMap = false;
		nodes.put(node, node);
	}
	public HashMap<Node, Node> getNodes()
	{
		return this.nodes;
	}
	
	public boolean containsGene(Node inNode, Node outNode)
	{
		SortedListIterator<Gene> i = genome.iterator();
		while(i.hasNext())
		{
			Gene g = i.next();
			if(g.getInNode().getNodeId() == inNode.getNodeId() && 
				g.getOutNode().getNodeId() == outNode.getNodeId())
			{
				return true;
			}
		}
		return false;
	}
	//dependency graph: 1st element for each element is the node dependent on all following nodes	
	public ArrayList<listNode<Node>> nodeDependencyGraph()
	{
		if(updatedDependencyGraph)
			return this.dependencyGraph;
		Set<Node> keys = this.nodes.keySet();
		Iterator<Node> nodeIterator = keys.iterator();
		dependencyGraph = new ArrayList<listNode<Node>>(keys.size());
		while(nodeIterator.hasNext())
		{
			Node node = nodeIterator.next();
			dependencyGraph.add(node.getNodeId(), new listNode<Node>(node));;
		}
		SortedListIterator<Gene> i = this.genome.iterator();
		while(i.hasNext())
		{
			Gene gene = i.next();
			if(gene.isEnabled())
			{
				listNode<Node> listNode = dependencyGraph.get(gene.getInNode().getNodeId());
				listNode<Node> tmp = new listNode<Node>(gene.getOutNode());
				tmp.next = listNode.next;
				listNode.next = tmp;
			}
		}
		updatedDependencyGraph = true;
		return dependencyGraph;
	}
	//here weight map is inNode and outNode to weight value
	public HashMap<Pair<Integer, Integer>, Double> genomeWeightMap()
	{
		if(updatedWeightMap)
			return this.weightMap;
		weightMap = new HashMap<Pair<Integer,Integer>, Double>();
		SortedListIterator<Gene> i = this.genome.iterator();
		while(i.hasNext())
		{
			Gene gene = i.next();
			int inNodeId = gene.getInNode().getNodeId();
			int outNodeId = gene.getOutNode().getNodeId();
			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(inNodeId, outNodeId);
			weightMap.put(pair, gene.getWeight());
		}
		updatedWeightMap = true;
		return weightMap;
	}
	public void mutateRandomWeight()
	{
		RandomGenerator randomGenerator = new RandomGenerator();
		Boolean[] actions = {true, false};
		double weight;
		double probs[] = {Globals.randomlyChangeWeightProbability, 100 - Globals.randomlyChangeWeightProbability};
		boolean randomlyMutate = (boolean) randomGenerator.probablityBasedAction(actions, probs);
		int randomGenomeIndex = randomGenerator.getRandomIntWithLimit(this.genomeSize());
		Gene randomGene = this.getGene(randomGenomeIndex);

		if(randomlyMutate)
			weight = randomGenerator.getRandomSignedDouble();
		else
		{
			Integer[] action = {-1, 1};
			int deltaSign = (int) randomGenerator.getRandomAction(action);
			double delta = (double)deltaSign * Globals.weightDelta;
			weight = randomGene.getWeight() + delta;
			weight = min(max(weight, Globals.minWeight), Globals.maxWeight);
		}
		randomGene.setWeight(weight);
		updatedWeightMap = false;
	}
	private double min(double a, double b)
	{
		return a > b ? b : a;
	}
	private double max(double a, double b)
	{
		return a > b ? a : b;
	}
	private int compareGenes(Gene arg0, Gene arg1) throws Exception
	{
		// TODO Auto-generated method stub
		if(arg0.getInovationNumber() < arg1.getInovationNumber())
			return -1;
		else if(arg0.getInovationNumber() > arg1.getInovationNumber())
			return 1;
		else
			throw new Exception("Connection already present");
	}
	@Override
	public int compare(Gene arg0, Gene arg1)
	{
		try
		{
			return compareGenes(arg0, arg1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
