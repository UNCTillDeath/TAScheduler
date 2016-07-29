package comp110.mlans13.genalgo;

import java.util.Random;

import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;

/**
 * 
 * @author mlans13
 *	
 *	This algorithm is meant to be a bit more out of left field, writing a genetic algorithm to evolve a 
 *	field of randomly generated schedules randomly into something which is fitting of the constraints of
 *	the problem.  While not necessarily a true genetic algorithm, given the way in which the sanitizer will be
 *	written in and thus not relying solely on random change, it still relies primarily on the functionality 
 *	of such things.
 */

public class GenAlgoStart implements SchedulingAlgo {
	
	// Create instance variables
	private int generations = 50;		// Evolve the population for a certain number of generations
//	private int endThreshold = 6;		// Also possible to run until the fitness surpasses a threshold value, such as 6.0

	// Run the entire bot
	public static void main(String[] args) {
		KarenBot bot = new KarenBot(new GenAlgoStart());
		
		// Using the most harsh assessment test in order to assess the final solution of the genetic algorithm
		String scenario = "real-world-approx-two-hour-chunks";
		int iterations = 50;			// Keep the iterations low, this thing is rather heavy
		
		// Run the genetic algorithm
		bot.run(scenario, iterations);
	}
	
	@Override
	public Schedule run(Schedule input, Random random) {
		Population pop = new Population(50, random);
		pop.genPopulation(input.getStaff(), input.getWeek());
		pop.printPopulation();

		return pop.getBestChromosome();
	}

}
