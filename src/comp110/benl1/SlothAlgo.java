package comp110.benl1;

import java.util.ArrayList;
import java.util.Random;

import comp110.Employee;
import comp110.Schedule;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

public class SlothAlgo {

  private Schedule _schedule;

  private Week _week;

  private Staff _staff;

  private ArrayList<Shift> _shifts;

  private ArrayList<ArrayList<Shift>> _possibleShifts;

  private ArrayList<Employee> _employees;

  private Random _random;
  
  private ArrayList<Schedule> _possibleSchedules;

  public Schedule run(Schedule input, Random random) {

    setup(input, random);

    assignSchedule();

    return input;
  }

  private void assignSchedule() {
    for (int i = 0; i < _shifts.size(); i++) {

      double highestScore = -1.0;

      ArrayList<Employee> availableEmployees = getAvailableEmployees(_shifts.get(i));

      fillPossibleShifts(availableEmployees, i);

    }
    //now _possibleShifts is a list with every list in it being all the possible shift combinations for that time slot
    //time to create every possible schedule
    _possibleSchedules = new ArrayList<Schedule>();
    
    
  }

  public static int factorial(int n) {
    int fact = 1; // this  will be the result
    for (int i = 1; i <= n; i++) {
      fact *= i;
    }
    return fact;
  }

  private void fillPossibleShifts(ArrayList<Employee> availableEmployees, int index) {
    //all possible combinations for this shift
    ArrayList<Shift> possibleShifts = new ArrayList<Shift>();

    _possibleShifts.add(possibleShifts);

    int shiftCapacity = _shifts.get(index).getCapacity();
    int numAvailable = availableEmployees.size();
    //num of possible combinations
    int possibleCombinations = factorial(numAvailable) / (factorial(shiftCapacity) * factorial(numAvailable - shiftCapacity));
    //keep track of which employees are being used for each possible shift
    int[] employeeIndeces = new int[shiftCapacity];
    //start each at corresponding values
    for (int i = 0; i < employeeIndeces.length; i++) {
      employeeIndeces[i] = i;
    }
    //loop for amount of possible situations
    for (int i = 0; i < possibleCombinations; i++) {
      //shift to be created
      Shift possibleShift = copyShift(_shifts.get(index));
      
      //add the employees into the shift
      for (int k = 0; k < employeeIndeces.length; k++) {
        possibleShift.add(availableEmployees.get(employeeIndeces[k]));
      }
      
      //keep track if an index was maxed
      boolean reset = false;
      
      //change values corresponding to next sequential combination
      for (int j = 0; j < employeeIndeces.length; j++) {
        //first one that is at its highest, add 1 to 1 before
        if (employeeIndeces[j] == numAvailable - (shiftCapacity - j) && j != 0) {
          reset = true;
          employeeIndeces[j - 1]++;
          //then for the rest set them each one higher
          for (int current = j; current < employeeIndeces.length; current++) {
            employeeIndeces[current] = employeeIndeces[current - 1] + 1;
          }
          break;
        }

      }
      
      if (!reset){
        employeeIndeces[shiftCapacity - 1]++;
      }

      possibleShifts.add(possibleShift);
    }

  }

  private Shift copyShift(Shift shift) {
    Shift newShift = new Shift(shift.getDay(), shift.getHour(), shift.getCapacity());
    return newShift;
  }

  private double scoreShift(Shift shift) {
    double score = 0.0;
    if (hasGenderBalance(shift)) score++;
    if (hasRequiredSkill(shift)) score++;

    return score;
  }

  private boolean hasRequiredSkill(Shift shift) {

    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee toAdd : shift) {
      totalSkill += toAdd.getLevel();
      numOfEmployees++;
    }

    return (totalSkill / numOfEmployees) >= 1.5;
  }

  private boolean hasGenderBalance(Shift shift) {
    //Can't have gender equality with 1 person
    if (shift.getCapacity() == 1) return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : shift) {
      if (e.getIsFemale()) hasFemale = true;
      if (!e.getIsFemale()) hasMale = true;
    }

    return hasMale && hasFemale;
  }

  private ArrayList<Employee> getAvailableEmployees(Shift shift) {
    ArrayList<Employee> availableEmployees = new ArrayList<Employee>();

    for (Employee e : _employees) {

      if (e.isAvailable(shift.getDay(), shift.getHour())) {
        availableEmployees.add(e);
      }

    }

    return availableEmployees;
  }

  private void setup(Schedule input, Random random) {

    _week = input.getWeek();

    _staff = input.getStaff();

    _employees = new ArrayList<Employee>();
    for (Employee e : _staff) {
      _employees.add(e);
    }

    _shifts = weekAsArray(_week);

    _possibleShifts = new ArrayList<ArrayList<Shift>>();

    _random = random;

  }

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
