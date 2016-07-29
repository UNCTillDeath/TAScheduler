package comp110.mlans13.genalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import comp110.Schedule;
import comp110.Staff;
import comp110.Week;

/**
 * 
 * @author mlans13
 *
 *	This class is used to contain the currently living Chromosomes, and is used to select
 *	the Chromosomes to be recombined as well as the Chromosomes to be replaced with children
 */

public class Population {
	
	// List of Chromosomes which make up the Population
	private List<Chromosome> contents;
	private int popSize;				// Size of the population
	private Random rand;				// The random instance to be used across the entire algorithm
	
	public Population(int size, Random r) {
		// Set the size of the population
		this.popSize = size;
		// Initialize the Random instance
		this.rand = r;
	}
	
	public void genPopulation(Staff staff, Week week) {
		// Create a ScheduleRandomizer to use
		ScheduleRandomizer gen = new ScheduleRandomizer(this.rand);
		
		// Initialize the contents ArrayList
		this.contents = new ArrayList<Chromosome>();
		
		// Generate the amount of Chromosomes to meet the popSize
		for (int i = 0; i < this.popSize; i++) {
			// Generate a new Schedule
			Schedule s = gen.genRandomSchedule(staff, week);
			// Assess the schedule's quality
			double qualityVal = TestingSandbox.testSchedule(s, this.rand);
			// Create a new Chromosome with this info and add it to contents
			contents.add(new Chromosome(s, qualityVal));
		}
		
		// Sort the ArrayList of Chromosomes from least to most fit 
		Collections.sort(this.contents);
	}
	
	// Debug function
	public void printPopulation() {
		int num = 1;
		for (Chromosome c: this.contents) {
			System.out.println(num + " " + c.getFitnessValue());
			num++;
		}
	}
 	
	
	// Getters
	public int getSize() {
		return this.popSize;
	}
	
	public Schedule getBestChromosome() {
		return this.contents.get(this.contents.size() - 1).getSchedule();
	}

}
