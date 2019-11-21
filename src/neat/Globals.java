package neat;


public class Globals {
	
	//speciation
	static double delta_Threshhold = 3.0;
	static double delta_C1 = 0.7;
	static double delta_C2 = 0.7;
	static double delta_C3 = 0.3;
	static int population_Normalization_Threshold = 20;
	//speciation
	
	//crossover
	static double enableDisableFlagProbablity = 75;
	//crossover
	
	//Selection
	static double populationSruvivalPercentage = 50;
	static double minimumPopulation = 5;
	//Selection
	
	//mutation
	static double weightMutationProbability = 60;
	static double connectionMutationProbability = 20;
	static double nodeMutationProbability = 20;
	static double randomlyChangeWeightProbability = 10;
	static double weightDelta = 0.01;
	//mutation
	
	//Gene Params
	static double minWeight = -1;
	static double maxWeight = 1;
	//Gene Params
}
