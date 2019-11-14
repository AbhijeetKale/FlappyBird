package neat;

public class Gene {
	private final int inNode;
	private final int outNode;
	private double weight;
	private boolean enabled; // true/false
	private final int inovationNumber;
	
	public Gene(int inNode, int outNode, double weight, boolean enabled, int inovationNumber)
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
	public int getInNode()
	{
		return this.inNode;
	}
	public int getOutNode()
	{
		return this.outNode;
	}
	public boolean isEnabled()
	{
		return this.enabled;
	}
}
