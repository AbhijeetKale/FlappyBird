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
		 * Input3: Velocity of bird
		 * Input4: x speed
		 * INPUT5: Acceleration
		*/
		Neat n = new Neat(GlobalVariables.inputCounts, GlobalVariables.outputCounts, 20) {
			@Override
			public double calculateFitnessScore(Genome genome) {
				// TODO Auto-generated method stub
				Main m = new Main();
				m.setNeatParamsToBird(this, genome);
				m.game();
				m.closeWindow();
				this.printGenome(genome);
				Stat birdStat = m.bird.getStats();
				birdStat.totalBirdJumps = max(1, birdStat.totalBirdJumps);
				double fitness = 10 * birdStat.pipesCrossed + 
								 birdStat.x_covered / birdStat.totalBirdJumps - 0.5 * abs(birdStat.y_diff_on_death);
				System.out.println("[Stats]: " + birdStat.x_covered + "-" + birdStat.totalBirdJumps);
				return fitness;
			}
		};
		while(generation < 20)
		{
			n.simulateGeneration();
			generation++;
			System.out.println("[Generation]: " + generation);
		}
	}
	private static int max(int a, int b)
	{
		return a > b ? a : b;
	}
	private static double abs(double x)
	{
		if(x < 0)
			x *= -1;
		return x;
	}
}
