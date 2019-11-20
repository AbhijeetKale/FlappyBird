package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Species implements Comparator<Genome>{

	private Genome representativeGenome = null;
	private SortedList<Genome> speciesPopulation;
	public Species()
	{
		this.speciesPopulation = new SortedList<Genome>(this);
		this.representativeGenome = null;
	}
	public Species(Genome g)
	{
		this.speciesPopulation = new SortedList<Genome>(this);
		speciesPopulation.add(g);
	}

	public Genome getRepresentativeGenome()
	{
		return this.representativeGenome;
	}
	public int getSpeciesPopulation()
	{
		return this.speciesPopulation.size();
	}
	private double abs(double s)
	{
		if(s < 0)
			s += -1;
		return s;
	}
	private int abs(int s)
	{
		if(s < 0)
			s += -1;
		return s;
	}
	public boolean genomeBelongsToSpecies(Genome genome)
	{
		if(this.representativeGenome == null)
		{
			return true;
		}
		double delta;
		double avgWeightDifference = 0;
		int disjointGenesDifference, excessGenesDifference;
		GenomePairData genomePairData = new GenomePairData(this.representativeGenome, genome);
		ArrayList<Pair<Gene, Gene>> matchingGenomes = genomePairData.getMatchingGenes();
		Pair<Gene, Gene> match;
		Iterator<Pair<Gene, Gene>> i = matchingGenomes.iterator();
		while(i.hasNext())
		{
			match = i.next();
			avgWeightDifference += abs(match.getKey().getWeight() + match.getValue().getWeight());
		}
		avgWeightDifference = avgWeightDifference / matchingGenomes.size();
		disjointGenesDifference = abs(genomePairData.getDisjointGenes1().size() - genomePairData.getDisjointGenes2().size());
		excessGenesDifference = abs(genomePairData.getExcessGenes1().size() - genomePairData.getExcessGenes2().size());
		delta = Globals.delta_C1 * (double)excessGenesDifference + Globals.delta_C2 * (double)disjointGenesDifference;
		if(this.speciesPopulation.size() > Globals.population_Normalization_Threshold)
			delta = delta / this.speciesPopulation.size();
		delta += Globals.delta_C3 * avgWeightDifference;
		
		return delta > Globals.delta_Threshhold ? false : true;
	}
	public void addGenome(Genome genome)
	{
		this.speciesPopulation.add(genome);
		this.representativeGenome = speciesPopulation.getFirstElement();
		
	}
	public void removeFromIndexToEnd(int fromIndex)
	{
		this.speciesPopulation.removeFromIndexToEnd(fromIndex);
		this.representativeGenome = speciesPopulation.getFirstElement();
	}
	
	public SortedListIterator<Genome> iterator()
	{
		return this.speciesPopulation.iterator();
	}
	
	@Override
	public int compare(Genome arg0, Genome arg1) {
		// TODO Auto-generated method stub
		if(arg0.getFitnessScore() < arg1.getFitnessScore())
			return 1;
		else if(arg0.getFitnessScore() > arg1.getFitnessScore())
			return -1;
		return 0;
	}
}
