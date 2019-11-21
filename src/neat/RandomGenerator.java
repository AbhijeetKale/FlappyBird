package neat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
	
	Random random;
	
	public RandomGenerator()
	{
		random = new Random();
	}
	/*Total should be == 100*/
	private int abs(int n)
	{
		if(n < 0)
			n *= -1;
		return n;
	}
	private double abs(double n)
	{
		if(n < 0.0)
			n *= -1;
		return n;
	}

	public Object probablityBasedAction(Object[] list, double[] probs)
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
	public Object getRandomAction(Object[] list)
	{
		Random r = new Random();
		int i = abs(r.nextInt()) % list.length;
		return list[i];
	}
	public Object getRandomAction(ArrayList<? extends Object> list)
	{
		Random r = new Random();
		int i = abs(r.nextInt()) % list.size();
		return list.get(i);
	}
	public int getRandomIntWithLimit(int n)
	{
		int i = abs(random.nextInt());
		return i % n;
	}
	
	public double getRandomSignedDouble()
	{
		int r = random.nextInt();
		double d = random.nextDouble();
		if(r % 2 == 0)
			return d;
		return -d;
	}
}
