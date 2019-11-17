package neat;

import java.util.Random;

public class RandomGenerator {
	/*Total should be == 100*/
	public static Object probablityBasedAction(Object[] list, double[] probs)
	{
		int sum = 0;
		Random r = new Random();
		double number = r.nextDouble() % 100;
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
}
