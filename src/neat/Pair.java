package neat;
/*Helper class created to store data in pairs*/
class Pair<A, B>
{
	private A data1;
	private B data2;
	public Pair()
	{
		this.data1 = null;
		this.data2 = null;
	}
	public Pair(A data1, B data2)
	{
		this.data1 = data1;
		this.data2 = data2;
	}
	public void setKey(A data1)
	{
		this.data1 = data1;
	}
	public void setValue(B data2)
	{
		this.data2 = data2;
	}
	public A getKey()
	{
		return this.data1;
	}
	public B getValue()
	{
		return this.data2;
	}
}