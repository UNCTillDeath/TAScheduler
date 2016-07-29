package comp110.mlans13.genalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import comp110.Employee;
import comp110.Schedule;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

/**
 * 
 * @author mlans13
 *
 *	This class is used to repetitively randomly generate bulk quantities of Schedules in 
 *	order to initially create the residents of the Population.
 */

public class ScheduleRandomizer {

	// Save the randomizer to be used for the assessor to work correctly
	private Random rand;
	static private final int GEN_OPTION_AMOUNT = 3; 	// Keep track of amount of generation method possibilities
	
	public ScheduleRandomizer(Random r) {
		this.rand = r;
	}
	
	/**
	 * Use this function in order to use any one of the multiple possible modes of generating the 
	 * Schedule at random
	 */
	public Schedule genRandomSchedule(Staff staff, Week week) {
		// Create a Schedule to save into
		Schedule workingSolution;
		
		// Randomly choose a function to run
		switch (this.rand.nextInt(GEN_OPTION_AMOUNT)) {
			case 0:
				workingSolution = genScheduleFrontToBack(staff, week);
				break;
			case 1:
				workingSolution = genScheduleBackToFront(staff, week);
				break;
			case 2:
				workingSolution = genScheduleShuffled(staff, week);
				break;
			default:
				return null;
		}
		
		// TODO: Perform randomly chosen post-processing on the workingSolution here
		
		return workingSolution;
	}
	
	/**
	 * Generate the Schedule front to back using first available staff
	 */
	public Schedule genScheduleFrontToBack(Staff staff, Week week) {
		Schedule workingSolution = new Schedule(staff.copy(), week.copy());
		
		// Flatten all the week's shifts into a 2d array
		ArrayList<Shift> shifts = this.weekAsArray(workingSolution.getWeek());
		
		// Iterate through the array and check per shift
		for (Shift shift: shifts) {
			// Check for each employee's availability that shift
			for (Employee e: workingSolution.getStaff()) {
				// Break out of the loop if there is no need for anyone more in the shift
				if (shift.getCapacityRemaining() <= 0)
					break;
				else {
					// Otherwise check if the employee is available to work that shift
					if (e.getCapacityRemaining() > 0 && e.isAvailable(shift.getDay(), shift.getHour()))
						shift.add(e);
				}
			}
		}
		
		
		return workingSolution;
	}
	
	/**
	 * Generate the Schedule back to front using first available staff
	 */
	public Schedule genScheduleBackToFront(Staff staff, Week week) {
		Schedule workingSolution = new Schedule(staff.copy(), week.copy());
		
		// Flatten all the week's shifts into a 2d array
		ArrayList<Shift> shifts = this.weekAsArray(workingSolution.getWeek());
		
		// Reverse the shifts
		Collections.reverse(shifts);
		
		// Iterate through the array and schedule per shift
		for (Shift shift: shifts) {
			// Check for each employee's availability that shift
			for (Employee e: workingSolution.getStaff()) {
				// Break out of the loop if there is no need for anyone more in the shift
				if (shift.getCapacityRemaining() <= 0)
					break;
				else {
					// Otherwise check if the employee is available to work that shift
					if (e.getCapacityRemaining() > 0 && e.isAvailable(shift.getDay(), shift.getHour()))
						shift.add(e);
				}
			}
		}
		
		return workingSolution;
	}
	
	/**
	 * Generate the schedule in a shuffled completely random order
	 */
	public Schedule genScheduleShuffled(Staff staff, Week week) {
		Schedule workingSolution = new Schedule(staff.copy(), week.copy());
		
		// Flatten all the week's shifts into a 2d array
		ArrayList<Shift> shifts = this.weekAsArray(workingSolution.getWeek());
		
		// Shuffle the shifts
		Collections.shuffle(shifts);
		
		// Iterate through the array and schedule per shift
		for (Shift shift: shifts) {
			// Check for each employee's availability that shift
			for (Employee e: workingSolution.getStaff()) {
				// Break out of the loop if there is no need for anyone more in the shift
				if (shift.getCapacityRemaining() <= 0)
					break;
				else {
					// Otherwise check if the employee is available to work that shift
					if (e.getCapacityRemaining() > 0 && e.isAvailable(shift.getDay(), shift.getHour()))
						shift.add(e);
				}
			}
		}
		
		return workingSolution;
	}
	
	/**
	 * Utility function used to flatten week shifts into an array'
	 * 
	 * Thanks Kris
	 */
	private ArrayList<Shift> weekAsArray(Week week) {
		ArrayList<Shift> buckets = new ArrayList<Shift>();
		for (Shift[] day: week.getShifts()) {
			for (Shift shift: day) {
				if (shift.getCapacity() > 0) {
					buckets.add(shift);
				}
			}
		}
		return buckets;
	}
}
