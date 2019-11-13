package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class Main {
	
	private Canvas canvas = null;
	private Bird bird;
	private Map map;
	private JPanel container;
	public void init()
	{
		container = new JPanel();
		container.setLayout(new OverlayLayout(container));
		
		bird = new Bird(GlobalVariables.BIRD_SIZE, GlobalVariables.BIRD_SIZE);
		if(canvas == null)
			canvas = new Canvas();
		map = new Map();
		GlobalVariables.osName = System.getProperty("os.name");
		if(GlobalVariables.osName.contentEquals("Linux"))
			GlobalVariables.animationSync = true;
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
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setVisible(true);
		canvas.addKeyListener(bird);
		
		bird.setBirdPosition(GlobalVariables.INIT_BIRD_X, GlobalVariables.INIT_BIRD_Y);
		bird.setOpaque(false);
		bird.addToView(map.getPipesInVIew());
		container.add(bird);
		
		map.setOpaque(false);
		container.add(map);		
		
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
		/*Game ends*/
		System.out.println("Game ended");
	}
	public void clearWindow()
	{
		bird.removeAll();
		map.removeAll();
		container.removeAll();
		canvas.removeAll();
	}
	public static void main(String[] args)
	{
		Main m = new Main();
		m.init();
		m.game();
		m.clearWindow();
	}
}
