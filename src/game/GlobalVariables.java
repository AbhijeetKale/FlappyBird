package game;

public class GlobalVariables {

	static final int C_HEIGHT = 700;
	static final int C_WIDTH = 500;
	/*Gap between top and bottom pipe*/
	static final int GAP = 230;
	/*Speed of map*/
	static final int MOVEMENT_X = 1;
	
	/*Frame rate for bird thread*/
	static final int DELAY_Y = 33;
	/*Frame rate for map thread*/
	static final int DELAY_X = 15;
	static final int DEAD_DELAY = 1000;
	
	static final int INIT_BIRD_X = 50;
	static final int INIT_BIRD_Y = 2 * C_HEIGHT / 5;
	static final int BIRD_SIZE = 40;
	static final int JUMP_VELOCITY = -17;
	
	static final int PIPE_WIDTH = 50;
	/*Interval between two pipes*/
	static final int PIPE_INTERVAL = 150;
	/*Number of pipes encountered before cleanup process*/
	static final int PIPE_COUNTER_TH= 100;
	static final int INIT_PIPE_HEIGHT = 200;
	static final int INIT_PIPE_X = GlobalVariables.C_WIDTH - 100;
	static final int INIT_PIPE_Y = GlobalVariables.C_HEIGHT + PIPE_WIDTH;
	/*Adjustment to Y alignment of pipes*/
	static final int PIPE_PLACEMENT_ADJUSTMENT = -30;
	static boolean isBirdAlive;
	
	static String osName;
	static boolean animationSync = false;
	
	
	/*Neat specific configs*/
	static final int inputCounts = 2;
	static final int outputCounts = 1;
	
}
