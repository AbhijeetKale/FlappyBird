package neat;

import java.util.HashSet;
import java.util.Iterator;

/*Represent the list of Genes/Connections in a Neural net, basically the neural itself*/
public class Genome {
	
	private SortedGeneList genome;
	private HashSet<Integer> connsPresent;	//inovation numbers hash
	public Genome()
	{
		genome = new SortedGeneList();
		connsPresent = new HashSet<Integer>();
	}
	public SortedListIterator iterator()
	{
		return this.genome.iterator();
	}
}
