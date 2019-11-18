package neat;
/*Has the info of each connection in the neural net*/
public class Gene {
	private final Node inNode;
	private final Node outNode;
	private double weight;
	private boolean enabled; // true/false
	private final int inovationNumber;
	
	public Gene(Node inNode, Node outNode, double weight, boolean enabled, int inovationNumber)
	{
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.enabled = enabled;
		this.inovationNumber = inovationNumber;
	}
	public void setWeight(double weight)
	{
		this.weight = weight;
	}
	public void setEnabledFlag(boolean flag)
	{
		this.enabled = flag;
	}
	public int getInovationNumber()
	{
		return this.inovationNumber;
	}
	public double getWeight()
	{
		return this.weight;
	}
	public Node getInNode()
	{
		return this.inNode;
	}
	public Node getOutNode()
	{
		return this.outNode;
	}
	public boolean isEnabled()
	{
		return this.enabled;
	}
}
