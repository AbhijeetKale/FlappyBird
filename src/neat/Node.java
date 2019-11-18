package neat;
enum NodeType
{
	INPUT,
	HIDDEN,
	OUTPUT
}
public class Node {
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
}
