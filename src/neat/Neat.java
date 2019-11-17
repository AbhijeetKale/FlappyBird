package neat;

import java.util.HashMap;

/*Main control class for NEAT algorithm*/

public abstract class Neat {
	

	private int globalInovationNumber;
	private HashMap<Pair<Integer, Integer>, Integer> existingGenes;	//global existing Genes
	
	public Neat()
	{
		this.existingGenes = new HashMap<Pair<Integer, Integer>, Integer>();
		this.globalInovationNumber = 1;
	}
	public void setSpeciationParameters(double deltaThreshold, double c1, double c2, double c3)
	{
		Globals.delta_Threshhold = deltaThreshold;
		Globals.delta_C1 = c1;
		Globals.delta_C2 = c2;
		Globals.delta_C3 = c3;
	}
	public abstract void calculateFitnessScore();
}
