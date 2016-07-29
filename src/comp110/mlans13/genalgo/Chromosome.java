package comp110.mlans13.genalgo;

import comp110.Schedule;

/**
 * 
 * @author mlans13
 *
 *	This class is the basic Chromosome.  It merely encapsulates the generated Schedule it represents and
 *	the fitness value(s) which it has been assessed by
 *
 *	Potential:
 *		Use it to calculate the aggregate/unit score of the various tests, gender, contiguousness, etc. 
 *		which can be performed on it.
 *
 *		At this point though it merely takes a single fitness value (potentially real world 2 hour chunk test)
 */

public class Chromosome implements Comparable<Chromosome> {

	private Schedule schedule;
	private double fitnessValue;
	
	public Chromosome(Schedule s, double fit) {
		this.schedule = s;
		this.fitnessValue = fit;
	}


	@Override
	public int compareTo(Chromosome c) {
		// Create the comparison value
		double compVal = this.getFitnessValue() - c.getFitnessValue();
		compVal *= 1000;
		
		return (int) compVal;
	}
	
	// Getters
	public Schedule getSchedule() {
		return this.schedule;
	}
	
	public double getFitnessValue() {
		return this.fitnessValue;
	}

}
