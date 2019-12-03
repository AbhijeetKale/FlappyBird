package game;

import java.awt.event.WindowEvent;
import java.lang.System.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import neat.Genome;
import neat.Neat;

public class Main {
	
	private Canvas canvas = null;
	private Bird bird;
	private Map map;
	private JPanel container;
	private JLabel neatLabel;
	public static int generation = 0;
	public Main()
	{
		container = new JPanel();
		container.setLayout(new OverlayLayout(container));
		
		bird = new Bird(GlobalVariables.BIRD_SIZE, GlobalVariables.BIRD_SIZE);
		if(canvas == null)
			canvas = new Canvas();
		map = new Map();
		GlobalVariables.isBirdAlive = true;
		GlobalVariables.osName = System.getProperty("os.name");
		if(GlobalVariables.osName.contentEquals("Linux"))
			GlobalVariables.animationSync = true;

		neatLabel = new JLabel("" + Main.generation);
	}
	public void dereferenceObjects()
	{
		this.canvas = null;
		this.bird = null;
		this.map= null;
		this.container = null;
	}
	public void game()
	{
		/*Game starts*/
		canvas.setSize(GlobalVariables.C_WIDTH, GlobalVariables.C_HEIGHT);
		canvas.setResizable(true);
		canvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		canvas.setVisible(true);
		canvas.addKeyListener(bird);
		
		bird.setBirdPosition(GlobalVariables.INIT_BIRD_X, GlobalVariables.INIT_BIRD_Y);
		bird.setOpaque(false);
		bird.addToView(map.getPipesInVIew());
		container.add(bird);
		
		map.setOpaque(false);
		container.add(map);		
		
		neatLabel.setOpaque(false);
		container.add(neatLabel);
		
		canvas.add(container);
		
		try
		{
			bird.getRunningThread().join();
			map.getRunningThread().join();
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void clearWindow()
	{
		bird.removeAll();
		map.removeAll();
		container.removeAll();
		canvas.removeAll();
	}
	public void setNeatParamsToBird(Neat n, Genome genome)
	{
		bird.setNeatParamsToBird(n, genome);
	}
	public void closeWindow()
	{
		canvas.dispatchEvent(new WindowEvent(canvas,  WindowEvent.WINDOW_CLOSING));
	}
	public static void main(String[] args)
	{
		/*
		 * Using 5 different inputs to neat:
		 * Input1: y displacement between bird and pipe gap
		 * Input2: x distance between bird and pipe
		 * INput3: X Speed of map
		 * input 4: y displacement between bird and 2nd pipe
		 * Input 5: Width of pipe
		 * Input 6: ACC
		*/
		Neat n = new Neat(GlobalVariables.inputCounts, GlobalVariables.outputCounts, 20) {
			@Override
			public double calculateFitnessScore(Genome genome) {
				// TODO Auto-generated method stub
				Main m = new Main();
				m.setNeatParamsToBird(this, genome);
				double runTime = System.currentTimeMillis();
				m.game();
				runTime = System.currentTimeMillis() - runTime;
				m.closeWindow();
				Stat birdStat = m.bird.getStats();
				//birdStat.totalBirdJumps = max(1, birdStat.totalBirdJumps);
				double fitness = 0.01 * runTime + 
								 - 0.1 * parse(birdStat.y_diff_on_death);
				return fitness;
			}
		};
		
		generation = 1;
		while(generation < 20)
		{
			n.simulateGeneration();
			System.out.println("[Generation]: " + generation++);
		}
	}
	private static double parse(double y)
	{
		if(y < 0)
			y = y * -1;
		return y;
	}

}
