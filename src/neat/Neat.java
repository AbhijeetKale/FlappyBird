package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*Main control class for NEAT algorithm*/

public abstract class Neat {
	

	private int globalInovationNumber;
	private HashMap<Pair<Integer, Integer>, Integer> existingGenes;	//global existing Genes
	private int inputNodes;
	private int outputNodes;
	private int initPopulationCount;
	private ArrayList<Species> speciesList;
	public Neat(int inputNodes, int outputNodes, int populationCount)
	{
		this.existingGenes = new HashMap<Pair<Integer, Integer>, Integer>();
		this.globalInovationNumber = 1;
		this.inputNodes= inputNodes;
		this.outputNodes = outputNodes;
		this.initPopulationCount = populationCount;
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
