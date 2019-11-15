package neat;

import java.util.ArrayList;
import java.util.Iterator;

/*Can be used for Crossover and speciation purposes*/

public class GenomePairData {
	
	private Genome genome1;
	private Genome genome2;
	private ArrayList<Pair<Gene, Gene>> matchingPairs;
	private ArrayList<Gene> disJointGenes1;
	private ArrayList<Gene> disJointGenes2;
	private ArrayList<Gene> excessGenes1;
	private ArrayList<Gene> excessGenes2;
	
	public GenomePairData(Genome g1, Genome g2)
	{
		this.genome1 = g1;
		this.genome2 = g2;
		matchingPairs = new ArrayList<Pair<Gene, Gene>>();
		disJointGenes1 = new ArrayList<Gene>();
		disJointGenes2 = new ArrayList<Gene>();
		excessGenes1 = new ArrayList<Gene>();
		excessGenes2 = new ArrayList<Gene>();
		createGenomePariData();
	}
	
	private void createGenomePariData()
	{
		Gene gene1 = null, gene2 = null;
		boolean iterate1Flag = true, iterate2Flag = true;
		int inNo1 = 0, inNo2 = 0;
		Iterator<Gene> i1 = this.genome1.iterator();
		Iterator<Gene> i2 = this.genome2.iterator();
		while(i1.hasNext() && i2.hasNext())
		{
			if(iterate1Flag)
				gene1 = i1.next();
			if(iterate2Flag)
				gene2 = i2.next();
			inNo1 = gene1.getInovationNumber();
			inNo2 = gene2.getInovationNumber();
			iterate1Flag = true;
			iterate2Flag = true;			
			if(inNo1 == inNo2)
				matchingPairs.add(new Pair<Gene, Gene>(gene1, gene2));
			else if(matchingPairs.size() == 0)
			{
				if(inNo1 < inNo2)
				{
					excessGenes1.add(gene1);
					iterate2Flag = false;
				}
				else
				{
					excessGenes2.add(gene2);
					iterate1Flag = false;
				}
			}
			else if(inNo1 < inNo2)
			{
				disJointGenes1.add(gene1);
				iterate2Flag = false;
			}
			else if(inNo1 > inNo2)
			{
				disJointGenes2.add(gene2);
				iterate1Flag = false;
			}
		}
		while(i1.hasNext())
			excessGenes1.add(i1.next());
		while(i2.hasNext())
			excessGenes2.add(i2.next());
	}
	
	public ArrayList<Pair<Gene, Gene>> getMatchingGenes()
	{
		return this.matchingPairs;
	}
	
	public ArrayList<Gene> getDisjointGenes1()
	{
		return this.disJointGenes1;
	}
	
	public ArrayList<Gene> getDisjointGenes2()
	{
		return this.disJointGenes2;
	}
	
	public ArrayList<Gene> getExcessGenes1()
	{
		return this.excessGenes1;
	}
	
	public ArrayList<Gene> getExcessGenes2()
	{
		return this.excessGenes2;
	}
}
