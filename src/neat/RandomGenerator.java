package neat;

import java.util.Random;

public class RandomGenerator {
	/*Total should be == 100*/
	private static int abs(int n)
	{
		if(n < 0)
			n *= -1;
		return n;
	}
	private static double abs(double n)
	{
		if(n < 0.0)
			n *= -1;
		return n;
	}

	public static Object probablityBasedAction(Object[] list, double[] probs)
	{
		double sum = 0;
		Random r = new Random();
		double number = abs(r.nextInt()) + abs(r.nextDouble());
		number = number % 100;
		for(int count = 0; count < probs.length; count++)
		{
			sum += probs[count];
			if(number <= sum)
			{
				return list[count];
			}
		}
		return -1;
	}
	public static Object getRandomAction(Object[] list)
	{
		Random r = new Random();
		int i = abs(r.nextInt()) % list.length;
		return list[i];
	}
}
