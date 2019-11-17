package neat;

import java.util.ArrayList;
import java.util.Iterator;

public class Species {

	private Genome representativeGenome = null;
	private int population = 0;
	private ArrayList<Gene> speciesPopulation;

	public Species()
	{
		this.speciesPopulation = new ArrayList<Gene>();
	}
	public Genome getRepresentativeGenome()
	{
		return this.representativeGenome;
	}
	public int getSpeciesPopulation()
	{
		return this.population;
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
		if(this.population > Globals.population_Normalization_Threshold)
		{
			delta = delta / this.population;
		}
		delta += Globals.delta_C3 * avgWeightDifference;
		
		return delta > Globals.delta_Threshhold ? false : true;
	}
	public void remove()
	{
		
	}
}
