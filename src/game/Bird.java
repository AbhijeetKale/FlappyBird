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
	private Stat birdStat;
	
	public Bird(int height, int width)
	{
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(GlobalVariables.BIRD_SIZE, GlobalVariables.C_HEIGHT));
		GlobalVariables.isBirdAlive = true;
		animator = new Thread(this);
		birdStat = new Stat();;
		initParams();
	}
	public Stat getStats()
	{
		return this.birdStat;
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
		boolean collision = false;
		Iterator<Pipe> iterator = pipesInView.iterator();
		int y_diff = 0, y_diff1, x_diff = 0;
		while(iterator.hasNext())
		{
			Pipe p = iterator.next();
			y_diff = GlobalVariables.C_HEIGHT - p.getHeight() - GlobalVariables.GAP;
			y_diff1 = y_diff + GlobalVariables.GAP
						  -GlobalVariables.BIRD_SIZE + GlobalVariables.PIPE_PLACEMENT_ADJUSTMENT;
			if(this.y < y_diff || this.y > y_diff1)
			{
				x_diff = p.getPositionX() - this.x;
				if(x_diff > 0 && x_diff < GlobalVariables.BIRD_SIZE)
				{
					collision = true;
					break;
				}
				else if(x_diff < 0 && -x_diff < GlobalVariables.PIPE_WIDTH)
				{
					collision = true;
					break;
				}
			}
		}
		return collision;
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
			killBird();
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
				killBird();
			}
			else if(y <= 0)
			{
				y = 0;
				if(velocity < 0)
					velocity = 0;
			}
		}
	}
	private void killBird()
	{
		Iterator<Pipe> iterator = pipesInView.iterator();
		int x_diff = -1;
		while(iterator.hasNext())
		{
			Pipe p = iterator.next();
			x_diff = p.getPositionX() - this.x;
			if(x_diff > 0)
			{
				this.birdStat.y_diff_on_death = this.y - (GlobalVariables.C_HEIGHT - p.getHeight() - GlobalVariables.GAP / 2);
				break;
			}
		}
		
		GlobalVariables.isBirdAlive= false;
	}
	private boolean jumpDecision()
	{
		Iterator<Pipe> iterator = pipesInView.iterator();
		int y_diff = 1, x_diff = -1, next_pipe_view = 0;
		double[] input = new double[GlobalVariables.inputCounts];
		input[3] = -10000;
		Pipe p;
		while(iterator.hasNext())
		{
			p = iterator.next();
			x_diff = p.getPositionX() - this.x + GlobalVariables.PIPE_WIDTH;
			if(x_diff > 0)
			{
				this.birdStat.pipesCrossed = max(0, p.getId() - 1);
				y_diff = this.y - (GlobalVariables.C_HEIGHT - p.getHeight() - GlobalVariables.GAP / 2);
				if(next_pipe_view == 1)
				{
					input[3] = y_diff; 
					break;
				}
				else
				{
					input[0] = y_diff;
					input[1] = x_diff;
				}
				next_pipe_view++;
			}
		}
		List<Double> output;
		input[2] = GlobalVariables.xSpeed;
		if(input[3] == -10000)
			input[3] = input[2];
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
			double outputX = output.get(0);

/*			String s = "[input] " + input[0] + "-" + input[1]
						+ "-" + input[2] + "-" + input[3] + "-" + input[4];
			System.out.println(s);
			System.out.println(outputX);
			System.out.println();*/
			return outputX > 0.5 ? true : false;
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
			{
				this.velocity = GlobalVariables.JUMP_VELOCITY;
				birdStat.totalBirdJumps++;
			}
			birdStat.x_covered += GlobalVariables.MOVEMENT_X;
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
	private int max(int a, int b)
	{
		return a > b ? a : b;
	}
}
