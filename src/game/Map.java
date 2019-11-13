package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.JPanel;



public class Map extends JPanel implements Runnable{

	private int pipeCounter = 0;
	private Thread map;
	private int score;
	private CustomList<Pipe> pipesInView;
	private class CleanUp implements Runnable
	{
		private Thread th;
		public CleanUp()
		{
			th = new Thread(this);
			th.start();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.gc();
		}
	}
	public Map()
	{
		this.score = 0;
		pipesInView = new CustomList<Pipe>();
		map = new Thread(this);
	}
	CustomList<Pipe> getPipesInVIew()
	{
		return this.pipesInView;
	}
	/*Map propagation function*/
	private void cycle()
	{
		if(pipeCounter % GlobalVariables.PIPE_INTERVAL == 0)
		{
			Random r = new Random();
			int ranHeight =  r.nextInt(400);
			Pipe p = new Pipe(ranHeight);
			pipesInView.add(p);
		}
		pipeCounter += 1;
		if(pipeCounter / GlobalVariables.PIPE_INTERVAL  >= GlobalVariables.PIPE_COUNTER_TH)
		{
			new CleanUp();
			pipeCounter = 0;
		}
	}
	Thread getRunningThread()
	{
		return this.map;
	}
	@Override
	public void addNotify() {
		// TODO Auto-generated method stub
		super.addNotify();
		map.start();
	}
	@Override 
	public void run() {
		// TODO Auto-generated method stub
 		while(GlobalVariables.isBirdAlive)
		{
			long beforetime, delay, timediff;
			beforetime = System.currentTimeMillis();
			cycle();
			repaint();
			if(GlobalVariables.animationSync)
				Toolkit.getDefaultToolkit().sync();
			timediff = System.currentTimeMillis() - beforetime;
			delay = GlobalVariables.DELAY_X - timediff;
			if(delay < 0)
			{
				delay = 2;
			}
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
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Pipe data;
		Graphics2D g2d = (Graphics2D) g;
		CustomIterator<Pipe> i = pipesInView.iterator();
		while(i.hasNext())
		{
			data = i.next();
			int x = data.getPositionX(); 	
			if(x <= -GlobalVariables.GAP)
			{
				pipesInView.removeHead();
				score++;
				continue;
			}
			data.setPositionX(x - GlobalVariables.MOVEMENT_X);
			g2d.setColor(Color.decode(data.getColor()));
			g2d.fillRect(x - GlobalVariables.MOVEMENT_X, 0, 
						GlobalVariables.PIPE_WIDTH, 
						GlobalVariables.C_HEIGHT - data.getHeight() - GlobalVariables.GAP);
			
			g2d.fillRect(x - GlobalVariables.MOVEMENT_X, 
						 GlobalVariables.C_HEIGHT - data.getHeight() + GlobalVariables.PIPE_PLACEMENT_ADJUSTMETN, 
						 GlobalVariables.PIPE_WIDTH, data.getHeight());
		}
	}
}
