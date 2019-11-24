package game;


public class Pipe{
	
	private int id;
	private int x;
	private int y;
	private int height;
	private final String color = "0x00ff00";
	public Pipe(int height, int id)
	{
		this.height = height;
		initParams();
	}
	private void initParams()
	{
		this.x = GlobalVariables.INIT_PIPE_X;
		this.y = GlobalVariables.INIT_PIPE_Y;
	}
	public int getHeight()
	{
		return this.height;
	}
	public void setPositionX(int x)
	{
		this.x = x;
	}
	public int getPositionX()
	{
		return this.x;
	}
	public String getColor()
	{
		return this.color;
	}
	public void setPipeHeight(int height)
	{
		this.height = height;
	}
	public int getId()
	{
		return this.id;
	}
}
