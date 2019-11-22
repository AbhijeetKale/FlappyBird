package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*Represent the list of Genes/Connections in a Neural net, basically the neural itself*/
public class Genome implements Comparator<Gene>{
	
	private SortedList<Gene> genome;
	private double fitness;
	private HashMap<Node, Node> nodes;
	
	public Genome()
	{
		genome = new SortedList<Gene>(this);
		nodes = new HashMap<Node, Node>();
		fitness = 0;
	}
	public void setFitnessScore(double fitness)	
	{
		this.fitness = fitness;
	}
	public SortedListIterator<Gene> iterator()
	{
		return this.genome.iterator();
	}
	public void addGene(Gene g)
	{
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

	public static Genome crossOver(Genome parent1, Genome parent2)
	{
		Genome child = new Genome();
		GenomePairData pairData = new GenomePairData(parent1, parent2);
		ArrayList<Pair<Gene, Gene>> matchingGenes = pairData.getMatchingGenes();
		ArrayList<Gene> disjointGenes1 = pairData.getDisjointGenes1();
		ArrayList<Gene> disjointGenes2 = pairData.getDisjointGenes2();
		ArrayList<Gene> excessGenes = null, excessGenes2 = null;
		RandomGenerator randomGenerator = new RandomGenerator();
		
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
		Pair<Gene, Gene> tmp;
		while(i.hasNext())
		{
			//matching gene code
			tmp = i.next();
			Gene[] geneArray = {tmp.getKey(), tmp.getValue()};
			double[] geneProbs = {50, 50};
			Gene gene = (Gene) randomGenerator.probablityBasedAction(geneArray, geneProbs);
			if(!tmp.getKey().isEnabled() || !tmp.getValue().isEnabled())
			{
				double[] enableDisable = {100 - Globals.enableDisableFlagProbablity, Globals.enableDisableFlagProbablity};
				Boolean[] b = {true, false};
				boolean flag = (boolean) randomGenerator.probablityBasedAction( b, enableDisable);
				gene.setEnabledFlag(flag);
			}
			child.addGene(gene);
		}
		
		Iterator<Gene> gi1 = disjointGenes1.iterator();
		while(gi1.hasNext())
			child.addGene(gi1.next());

		Iterator<Gene> gi2 = disjointGenes2.iterator();
		while(gi2.hasNext())
			child.addGene(gi2.next());
		
		Iterator<Gene> ei1 = excessGenes.iterator();
		while(ei1.hasNext())
			child.addGene(ei1.next());

		if(excessGenes2 != null)
		{
			Iterator<Gene> ei2 = excessGenes2.iterator();
			while(ei2.hasNext())
				child.addGene(ei2.next());
		}
		
		return child;
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
			if(g.getInNode() == inNode && g.getOutNode() == outNode)
			{
				return true;
			}
		}
		return false;
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
