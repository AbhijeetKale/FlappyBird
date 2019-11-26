package neat;
enum NodeType
{
	INPUT,
	HIDDEN,
	OUTPUT
}
public class Node implements Cloneable{
	private int nodeId;
	private NodeType type;
	public Node(int id, NodeType type)
	{
		this.nodeId = id;
		this.type = type;
	}
	public int getNodeId()
	{
		return this.nodeId;
	}
	public NodeType getNodeType()
	{
		return this.type;
	}
	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		Integer id = (Integer) nodeId;
		return id.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		Node n = (Node) obj;
		if(n.getNodeId() == this.nodeId)
			return true;
		return false;
	}
	protected Object clone() throws CloneNotSupportedException {
		Node clone = new Node(this.nodeId, this.type);
		return clone;
		
	};
}
