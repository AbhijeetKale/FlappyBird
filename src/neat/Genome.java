package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/*Represent the list of Genes/Connections in a Neural net, basically the neural itself*/
public class Genome implements Comparator<Gene>{
	
	private SortedList<Gene> genome;
	private double fitness;
	public Genome()
	{
		genome = new SortedList<Gene>(this);
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
		
		if(parent1.getFitnessScore() == parent1.getFitnessScore())
		{
			excessGenes = pairData.getExcessGenes1();
			excessGenes2 = pairData.getExcessGenes2();
		}
		else if(parent1.getFitnessScore() < parent1.getFitnessScore())
		{
			excessGenes = pairData.getExcessGenes1();		
			excessGenes2 = null;
		}
		else if(parent1.getFitnessScore() < parent1.getFitnessScore())
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
			Gene gene = (Gene) RandomGenerator.probablityBasedAction(geneArray, geneProbs);
			if(!tmp.getKey().isEnabled() || !tmp.getValue().isEnabled())
			{
				double[] enableDisable = {100 - Globals.enableDisableFlagProbablity, Globals.enableDisableFlagProbablity};
				Boolean[] b = {true, false};
				boolean flag = (boolean) RandomGenerator.probablityBasedAction( b, enableDisable);
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
	@Override
	public int compare(Gene arg0, Gene arg1) {
		// TODO Auto-generated method stub
		if(arg0.getInovationNumber() < arg1.getInovationNumber())
			return -1;
		else if(arg0.getInovationNumber() > arg1.getInovationNumber())
			return 1;
		return 0;
	}
}
