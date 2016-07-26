package comp110.benl1;

import java.util.ArrayList;
import java.util.Random;

import comp110.Employee;
import comp110.Schedule;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

public class MailboxAlgo {

  private Schedule _schedule;

  private Week _week;

  private Staff _staff;

  private Shift[][] _shifts;

  private ArrayList<Employee> _employees;

  private Random _random;

  private int _shiftAttempt;

  private static final int SHIFT_FILL = 1000;

  private static final int LATEST_SHIFT = 23;

  public Schedule run(Schedule input, Random random) {

    setup(input, random);

    assignShifts();

    return input;
  }

  private void setup(Schedule input, Random random) {

    _week = input.getWeek();

    _staff = input.getStaff();

    //fill up list of staff
    _employees = new ArrayList<Employee>();
    for (Employee e : _staff) {
      _employees.add(e);
    }

    _shifts = _week.getShifts();

    _random = random;

  }
  /***
   * Steps:
   * 
   */
  private void assignShifts() {
    //
    assignMF();

  }

  private void assignMF() {

    for (int i = 0; i < _shifts.length; i++) {
      for (int j = 0; j < _shifts[0].length; j++) {

        _shiftAttempt = 0;

        if (_shifts[i][j].getCapacity() > 1) {
          //get any male
          boolean male = addMale(i, j, 1, 3);
          //if successfully found a male, continue with plan to find female
          if (male) {
            //boolean female = addFemale(i, j);
          }
        }

      }
    }

  }

  private boolean scheduleEmployee(Employee e, int day, int hour) {
    boolean success = false;

    if (e.isAvailable(day, hour) && e.getCapacityRemaining() > 0) {

      success = _shifts[day][hour].add(e);

      if (e.getCapacityRemaining() == 0) _employees.remove(e);
    }

    return success;
  }

  private boolean addMale(int day, int hour, double minSkill, double maxSkill) {

    int randomStart = _random.nextInt(_employees.size());

    //look for male that is free at this time in staff
    for (int i = randomStart; i != randomStart; i++) {

      //want to have some randomness so start from a random index and then loop back
      if (i > LATEST_SHIFT) i = 0;

      Employee e = _employees.get(i);
      if (e.isAvailable(day, hour) && !e.getIsFemale() && (e.getLevel() <= maxSkill && e.getLevel() >= minSkill)) {

        if (scheduleEmployee(e, day, hour)) return true;

      }

    }

    return false;
  }

  private boolean addFemale(int day, int hour, double minSkill, double maxSkill) {

    int randomStart = _random.nextInt(_employees.size());

    //look for female that is free at this time in staff
    for (int i = randomStart; i != randomStart; i++) {

      //want to have some randomness so start from a random index and then loop back
      if (i > LATEST_SHIFT) i = 0;

      Employee e = _employees.get(i);
      if (e.isAvailable(day, hour) && e.getIsFemale() && (e.getLevel() <= maxSkill && e.getLevel() >= minSkill)) {

        if (scheduleEmployee(e, day, hour)) return true;

      }

    }

    return false;

  }

  private boolean scheduleRandom(int day, int hour) {

    int randomStart = _random.nextInt(_employees.size());

    for (int i = randomStart; i != randomStart; i++) {
      if (i > LATEST_SHIFT) i = 0;

      Employee e = _employees.get(i);
      if (e.isAvailable(day, hour)) {

        if (scheduleEmployee(e, day, hour)) return true;

      }

    }

    return false;
  }

  //IMPLEMENT THIS LAST
  private void attemptContiguous(Employee e, int day, int hour) {

  }

}
