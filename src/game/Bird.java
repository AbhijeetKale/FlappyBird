package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import neat.Genome;
import neat.InCorrectInputException;
import neat.Neat;
import others.Mutex;

	
public class Bird extends JPanel implements Runnable, KeyListener{
	private int x = 0;
	private int y = 0;
	private final int width;
	private final int height;
	private final int ACC = 2;
	private String color = "0xffff00";
	private int velocity = 0;
	private Thread animator;
	private CustomList<Pipe> pipesInView;
	private Genome genome;
	private Neat neat;
	public Bird(int height, int width)
	{
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(GlobalVariables.BIRD_SIZE, GlobalVariables.C_HEIGHT));
		GlobalVariables.isBirdAlive = true;
		animator = new Thread(this);
		initParams();
	}
	public void setNeatParamsToBird(Neat n, Genome genome)
	{
		this.genome = genome;
		this.neat = n;
	}

	public void setBirdPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public void addToView(CustomList<Pipe> pipesInView)
	{
		this.pipesInView = pipesInView;
	}
	private void initParams()
	{
		y = GlobalVariables.INIT_BIRD_X;
		x = GlobalVariables.INIT_BIRD_Y;
		velocity = 0;
	}
	Thread getRunningThread()
	{
		return this.animator;
	}
	@Override
	public void addNotify() {
		// TODO Auto-generated method stub
		super.addNotify();
		animator.start();
	}
	/*Detect collision between bird and pipes*/
	private boolean Collision()
	{
		Iterator<Pipe> iterator = pipesInView.iterator();
		while(iterator.hasNext())
		{
			Pipe p = iterator.next();
			int y_diff = GlobalVariables.C_HEIGHT - p.getHeight() - GlobalVariables.GAP;
			int y_diff1 = y_diff + GlobalVariables.GAP
						  -GlobalVariables.BIRD_SIZE + GlobalVariables.PIPE_PLACEMENT_ADJUSTMENT;
			if(this.y < y_diff || this.y > y_diff1)
			{
				int x_diff = p.getPositionX() - this.x;
				if(x_diff > 0 && x_diff < GlobalVariables.BIRD_SIZE)
				{
					return true;
				}
				else if(x_diff < 0 && -x_diff < GlobalVariables.PIPE_WIDTH)
					return true;
			}
		}
		return false;
	}
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.decode(color));
		g2d.fillOval(x, y, width, height);
	}
	/*Bird propagation function*/
	private void cycle()
	{
		if(Collision())
		{
			color = "0xff0000";
			GlobalVariables.isBirdAlive = false;
		}
		else
		{
			color = "0xffff00";
			y += velocity;
			synchronized(Mutex.class)
			{
				velocity += ACC;
			}
			if(y >= GlobalVariables.C_HEIGHT - height - 1)
			{
				y = GlobalVariables.C_HEIGHT - height - 1;
				GlobalVariables.isBirdAlive= false;
			}
			else if(y <= 0)
			{
				y = 0;
				if(velocity < 0)
					velocity = 0;
			}
		}
	}
	private boolean jumpDecision()
	{
		Iterator<Pipe> iterator = pipesInView.iterator();
		int y_diff = 1, x_diff = -1;
		while(iterator.hasNext())
		{
			Pipe p = iterator.next();
			x_diff = p.getPositionX() - this.x;
			if(x_diff > 0)
			{
				y_diff = GlobalVariables.C_HEIGHT - p.getHeight() - GlobalVariables.GAP;
				break;
			}
		}
		List<Double> output;
		double[] input = new double[GlobalVariables.inputCounts];
		input[0] = y_diff;
		input[1] = x_diff;
		input[2] = this.velocity;
		input[3] = GlobalVariables.MOVEMENT_X;
		input[4] = this.ACC;
		try
		{
			output = neat.calculateOutputForGenome(this.genome, input);
		}
		catch(InCorrectInputException e)
		{
			e.printStackTrace();
			return false;
		}
		if(output.size() > 0)
		{
			double x = output.get(0);
			System.out.println("input1 = " + input[0]);
			System.out.println("input2 = " + input[1]);
			System.out.println("input3 = " + input[2]);
			System.out.println("input4 = " + input[3]);
			System.out.println("input5 = " + input[4]);
			System.out.println(x);
			System.out.println();
			return x >= 0.5 ? true : false;
		}
		return false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		long beforetime, delay, timediff;
		while(GlobalVariables.isBirdAlive)
		{
			beforetime = System.currentTimeMillis();
			if(jumpDecision())
				this.velocity = GlobalVariables.JUMP_VELOCITY;
			cycle();
			repaint();
			timediff = System.currentTimeMillis() - beforetime;
			delay = GlobalVariables.DELAY_Y - timediff;
			if(delay < 0)
				delay = 2;
			try {
				Thread.sleep(delay);
			}
			catch(InterruptedException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
			synchronized(Mutex.class)
			{
				//this.velocity = GlobalVariables.JUMP_VELOCITY;
				GlobalVariables.isBirdAlive = false;
			}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
