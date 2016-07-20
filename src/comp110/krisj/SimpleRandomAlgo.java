package comp110.krisj;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Employee;
import comp110.KarenBot;
import comp110.Week;

/*
 * This is a very naive, very poor algorithm.
 * 
 * However, it does generate valid schedules. As such, it's intended to be a simple example that shows off how
 * employees are added to shifts.
 * 
 * The algorithm is:
 * 
 * 1) flattens the 2D array of shifts into a 1D array
 * 
 * 2) shuffles the resultingi array
 * 
 * 3) fill each shift with the correct number of employees by looping through all employees, seeing if they're available, and adding them
 */
public class SimpleRandomAlgo implements SchedulingAlgo {

  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new SimpleRandomAlgo());
    // Change the following string to a different directory name in the 'data'
    // folder to try other scenarios.
    String scenario = "real-world-approx-two-hour-chunks";
    int trials = 1000;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule schedule, Random random) {
    Week week = schedule.getWeek();

    // Flatten the 2D array of shifts.
    ArrayList<Shift> shifts = this.weekAsArray(week);

    // Shuffle the order of the shifts to add entropy.
    Collections.shuffle(shifts, random);

    // For each shift, for each employee until the shift is full, schedule
    // available employees.
    for (Shift shift : shifts) {
      for (Employee employee : schedule.getStaff()) {
        if (shift.getCapacityRemaining() == 0) {
          break;
        } else {
          if (employee.getCapacityRemaining() > 0 && employee.isAvailable(shift.getDay(), shift.getHour())) {
            shift.add(employee);
          }
        }
      }
    }

    return schedule;
  }

  /**
   * Turn the 2D array of shifts into a 1D ArrayList
   * 
   * @param week
   * @return
   */
  private ArrayList<Shift> weekAsArray(Week week) {
    ArrayList<Shift> buckets = new ArrayList<Shift>();
    for (Shift[] day : week.getShifts()) {
      for (Shift shift : day) {
        if (shift.getCapacity() > 0) {
          buckets.add(shift);
        }
      }
    }
    return buckets;
  }

}