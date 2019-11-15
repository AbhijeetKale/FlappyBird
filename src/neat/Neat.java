package neat;

import java.util.HashMap;

/*Main control class for NEAT algorithm*/

public class Neat {
	

	private int globalInovationNumber;
	private HashMap<Pair<Integer, Integer>, Integer> existingGenes;	//global existing Genes
	
	public Neat()
	{
		this.existingGenes = new HashMap<Pair<Integer, Integer>, Integer>();
		this.globalInovationNumber = 1;
	}
}
