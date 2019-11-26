package neat;
/*Has the info of each connection in the neural net*/
public class Gene implements Cloneable{
	private final Node inNode;		//connection which goes into a node
	private final Node outNode;		// connection which comes out of a node
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
	public String toString()
	{
		String str = "";
		str += "{outNode = " + outNode.getNodeId() + " NodeType = " + outNode.getNodeType();
		str += ", inNode = " + inNode.getNodeId() + " NodeType = " + inNode.getNodeType();
		str += ", weight = " + weight;
		str += ", enabled = " + enabled;
		str += ", inovationNumber = " + inovationNumber + "}";
		return str;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Gene clone = new Gene((Node) this.inNode.clone(), (Node) this.outNode.clone(), this.weight, this.enabled, this.inovationNumber);
		return clone;
	}
}
