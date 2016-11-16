package comp110.FXCrew;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Threader;
import comp110.Week;

public class FXAlgo implements SchedulingAlgo {

  private Schedule            _schedule;

  private Week                _week;

  private Staff               _staff;

  private List<Shift>         _shifts;

  private ArrayList<Employee> _employees;

  private Random              _random;

  private static final int    SHIFT_FILL_ATTEMPTS = 100;

  @Override
  public Schedule run(Schedule input, Random random) {

    setup(input, random);

    scheduleStaff();

    // scheduleRemainingEmployees();

    testSchedule();

    attemptOneHourFix();

    scheduleRemainingEmployees();

    return input;
  }

  private void setup(Schedule input, Random random) {

    _schedule = input;

    ShiftsByStaffConstraints sorter = new ShiftsByStaffConstraints(input);
    sorter.loadSchedule(input);

    _week = input.getWeek();

    _staff = input.getStaff();

    _employees = new ArrayList<Employee>();
    for (Employee e : _staff) {
      ChunkEmployee ce = new ChunkEmployee(e, _week);
      // System.out.println(ce.getName());
      for (Chunk c : ce.getChunks()) {
        // System.out.println(c);
      }
      _employees.add(ce);
    }

    _shifts = sorter.getSortedShifts();

    _random = random;

  }

  private void scheduleStaff() {

    for (int i = 0; i < _shifts.size(); i++) {

      int shiftAttempts = 0;

      while (_shifts.get(i).getCapacityRemaining() > 0) {

        if (shiftAttempts > SHIFT_FILL_ATTEMPTS) {
          break;
        }

        Shift shift = _shifts.get(i);
        // System.out.println(isRealShift(shift));

        ArrayList<Employee> availableEmployees = getAvailableEmployees(shift);

        ArrayList<ScoredEmployee> scoredEmployees = scoreEmployees(availableEmployees, shift);

        addHighestScoredEmployee(scoredEmployees, shift);

        shiftAttempts++;
      }

    }
  }

  private boolean isRealShift(Shift shift) {
    Shift[][] shifts = _schedule.getWeek().getShifts();
    for (int i = 0; i < shifts.length; i++) {
      for (int j = 0; j < shifts[0].length; j++) {
        if (shift == shifts[i][j])
          return true;
      }
    }
    return false;
  }

  // return a list of the avialable employees for a given shift
  private ArrayList<Employee> getAvailableEmployees(Shift shift) {
    ArrayList<Employee> availableEmployees = new ArrayList<Employee>();

    for (Employee e : _employees) {

      if (e.isAvailable(shift.getDay(), shift.getHour())) {
        availableEmployees.add(e);
      }

    }

    return availableEmployees;
  }

  // return a list of scored employees
  private ArrayList<ScoredEmployee> scoreEmployees(ArrayList<Employee> availableEmployees, Shift shift) {
    ArrayList<ScoredEmployee> scoredEmployees = new ArrayList<ScoredEmployee>();
    int hundred = 0;
    for (Employee e : availableEmployees) {
      double score = 0.0;

      // scoring goes here

      // gender
      if (missingGender(shift) == 0 || hasGenderBalance(shift)) {
        if (majorityRemaining() == 1) {
          if (!e.getIsFemale()) {
            score += .25;
          }
        } else {
          if (e.getIsFemale()) {
            score += .25;
          }
        }
      } else {
        if (e.getIsFemale() && missingGender(shift) == 2) {
          score++;
        }
        if (!e.getIsFemale() && missingGender(shift) == 1) {
          score++;
        }
      }

      if (getPotentialSkill(e, shift) < 1.5) {

      } else {
        // using equation y = (-1/3)x + 1.5 where x is employees' skill.
        // equation normalizes the score to be given between 0 and 1
        score += (-1.0 / 3.0) * (double) e.getLevel() + 1.5;
      }

      if (isContiguous(e, shift)) {
        // System.out.println("hi");

        score += 1.0;
      } else if (e.isAvailable(shift.getDay(), shift.getHour() - 1) || e.isAvailable(shift.getDay(), shift.getHour() + 1)) {
        // System.out.println("running");
        score += 1.0;
      }

      if (isScheduled(e, shift)) {
        score = -.5;
      }

      scoredEmployees.add(new ScoredEmployee(e, score));
    }
    // System.out.println(hundred);
    return scoredEmployees;
  }

  private boolean isScheduled(Employee e, Shift shift) {
    for (Employee e1 : shift) {
      if (e == e1) {
        return true;
      }
    }
    return false;
  }

  // try to add highest scored employee out of list, return success of attempt
  private boolean addHighestScoredEmployee(ArrayList<ScoredEmployee> scoredEmployees, Shift shift) {
    // keep list of equally high scores

    if (scoredEmployees.size() == 0) {
      return false;
    }

    double highestScore = -1.0;

    // An arraylist with all the employees with the highest score
    ArrayList<Employee> highestScores = new ArrayList<>();
    for (ScoredEmployee e : scoredEmployees) {
      highestScore = Math.max(e.getScore(), highestScore);
    }

    for (ScoredEmployee e : scoredEmployees) {
      if (e.getScore() == highestScore) {
        highestScores.add(e.getEmployee());
      }
    }

    // Randomly chooses the index of one of the employees tied for highest score
    int index = _random.nextInt(highestScores.size());
    // System.out.println(hasHighest.getEmployee().getName());
    // System.out.println(highestScores.size());

    // System.out.println("=========== Day: " + shift.getDay() + " Hour: " +
    // shift.getHour() + "===========");
    // for (ScoredEmployee e : scoredEmployees) {
    // String debug = String.format("%15s %4f", e.getEmployee().getName(),
    // e.getScore());
    // System.out.println(debug);
    // }
    ChunkEmployee chunkEmployee = (ChunkEmployee) highestScores.get(index);
    Chunk chunkOfShift = chunkEmployee.getChunk(shift);

    boolean success = chunkOfShift.scheduleEmployeeToChunk();
    if (highestScores.get(index).getCapacityRemaining() == 0) {
      _employees.remove(highestScores.get(index));
    }

    // if (success) System.out.println("added");
    return success;
  }

  private double getPotentialSkill(Employee e, Shift shift) {
    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee toAdd : shift) {
      totalSkill += toAdd.getLevel();
      numOfEmployees++;
    }

    // add employee
    totalSkill += e.getLevel();
    numOfEmployees++;

    return totalSkill / numOfEmployees;
  }

  private boolean hasGenderBalance(Shift shift) {
    // Can't have gender equality with 1 person
    if (shift.getCapacity() == 1)
      return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : shift) {
      if (e.getIsFemale())
        hasFemale = true;
      if (!e.getIsFemale())
        hasMale = true;
    }

    return hasMale && hasFemale;
  }

  // 0=missing both 1=missing male 2=missing female
  private int missingGender(Shift shift) {

    if (shift.getCapacityRemaining() == shift.getCapacity()) {
      return 0;
    }

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : shift) {
      if (e.getIsFemale())
        hasFemale = true;
      if (!e.getIsFemale())
        hasMale = true;
    }

    if (!hasMale) {
      return 1;
    } else {
      return 2;
    }

  }

  private boolean possibleHasGenderBalance(Employee toAdd, Shift shift) {

    // Can't have gender equality with 1 person
    if (shift.getCapacity() == 1)
      return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : shift) {
      if (e.getIsFemale())
        hasFemale = true;
      if (!e.getIsFemale())
        hasMale = true;
    }

    if (toAdd.getIsFemale())
      hasFemale = true;
    else
      hasMale = true;

    return hasMale && hasFemale;
  }

  private boolean possibleHasGenderBalance(Employee toAdd, Employee toRemove, Shift shift) {

    // Can't have gender equality with 1 person
    if (shift.getCapacity() == 1)
      return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : shift) {
      if (toRemove == e) {
        continue;
      }
      if (e.getIsFemale())
        hasFemale = true;
      if (!e.getIsFemale())
        hasMale = true;
    }

    if (toAdd.getIsFemale())
      hasFemale = true;
    else
      hasMale = true;

    return hasMale && hasFemale;
  }

  private boolean isContiguous(Employee e, Shift shift) {

    // if (shift.getHour() == 0 && shift.getHour() == 23) {
    // return false;
    // }

    if (shift.getHour() != 0) {
      Shift before = _week.getShift(shift.getDay(), shift.getHour() - 1);

      for (Employee e1 : before) {
        if (e1 == e) {
          return true;
        }
      }
    }

    if (shift.getHour() != 23) {
      Shift after = _week.getShift(shift.getDay(), shift.getHour() + 1);

      for (Employee e1 : after) {
        if (e1 == e) {
          return true;
        }
      }
    }
    return false;
  }

  // males = 1 females = 0
  private int majorityRemaining() {
    int maleHours = 0;
    int femaleHours = 0;

    for (Employee e : _staff) {
      if (e.getIsFemale()) {
        femaleHours += e.getCapacityRemaining();
      } else {
        maleHours += e.getCapacityRemaining();
      }
    }

    return maleHours > femaleHours ? 1 : 0;
  }

  public static void main(String[] args) {

    KarenBot karenBot = new KarenBot(new FXAlgo());
    Threader threader = new Threader(karenBot, 8, "week1", 100);
    // karenBot.run("week1", 1000);

  }

  private void scheduleRemainingEmployees() {
    ArrayList<Employee> hasRemaining = getEmployeesWithRemainingCapacity();
    // System.out.println(hasRemaining.size());

    // first time through we only schedule with shifts that have capacity
    // remaining
    for (Employee e : hasRemaining) {

      ArrayList<Shift> availableShifts = getShiftsAvailable(e);

      for (Shift shift : availableShifts) {
        // if (possibleHasRequiredSkill(e, shift)) {
        if (shift.getCapacityRemaining() > 0 && e.getCapacityRemaining() > 0) {
          shift.add(e);
        }
      }
    }

    // second time through we only care about filling Employees remaining
    // capacity
    hasRemaining = getEmployeesWithRemainingCapacity();
    for (Employee e : hasRemaining) {

      ArrayList<Shift> availableShifts = getShiftsAvailable(e);

      boolean scheduled = false;

      for (Shift shift : availableShifts) {
        // if (possibleHasRequiredSkill(e, shift)) {
        if (e.getCapacityRemaining() > 0) {
          scheduled = true;
          shift.add(e);
        }
      }
      // if (!scheduled) System.out.println("crap");
    }

  }

  private ArrayList<Employee> getEmployeesWithRemainingCapacity() {
    ArrayList<Employee> hasRemaining = new ArrayList<Employee>();

    // put all employees with remaining hours in a list
    for (int i = 0; i < _employees.size(); i++) {
      if (_employees.get(i).getCapacityRemaining() > 0) {
        hasRemaining.add(_employees.get(i));
      }
    }
    return hasRemaining;
  }

  private boolean possibleHasRequiredSkill(Employee toAdd, Shift shift) {

    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee e : shift) {
      totalSkill += e.getLevel();
      numOfEmployees++;
    }

    // "add" employee
    totalSkill += toAdd.getLevel();
    numOfEmployees++;

    return (totalSkill / numOfEmployees) >= 1.5;
  }

  private boolean possibleHasRequiredSkill(Employee toAdd, Employee toRemove, Shift shift) {

    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee e : shift) {
      if (e == toRemove)
        continue;
      totalSkill += e.getLevel();
      numOfEmployees++;
    }

    // "add" employee
    totalSkill += toAdd.getLevel();
    numOfEmployees++;

    return (totalSkill / numOfEmployees) >= 1.5;
  }

  private ArrayList<Shift> getShiftsAvailable(Employee e) {
    ArrayList<Shift> availableShifts = new ArrayList<Shift>();

    for (Shift shift : _shifts) {
      if (e.isAvailable(shift.getDay(), shift.getHour())) {
        availableShifts.add(shift);
      }
    }

    return availableShifts;

  }

  private boolean hasRequiredSkill(Shift shift) {
    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee e : shift) {

      totalSkill += e.getLevel();
      numOfEmployees++;
    }

    return (totalSkill / numOfEmployees) >= 1.5;
  }

  private void attemptOneHourFix() {
    // need to fill this with all employees scheduled for 1 hour shifts
    ArrayList<IssueEmployee> issues = new ArrayList<IssueEmployee>();

    for (int i = 0; i < _shifts.size(); i++) {

      for (Employee e : _shifts.get(i)) {
        boolean isContiguous = false;

        Shift shift = _shifts.get(i);
        if (!isScheduled(e, shift))
          System.out.println("what the actual f");

        if (_shifts.get(i).getHour() > 11) {
          if (isScheduled(e, dayHourToShift(shift.getDay(), shift.getHour() - 1))) {
            isContiguous = true;
          }
        }

        if (_shifts.get(i).getHour() < 20) {
          if (isScheduled(e, dayHourToShift(shift.getDay(), shift.getHour() + 1))) {
            isContiguous = true;
          }
        }

        if (!isContiguous) {
          issues.add(new IssueEmployee(e, shift));
        }

      }

    }

    boolean swapped = false;

    for (IssueEmployee e : issues) {
      for (int i = 0; i < issues.size(); i++) {
        IssueEmployee other = issues.get(i);
        if (e != other) {

          if (e.getEmployee().isAvailable(other.getShift().getDay(), other.getShift().getHour()) && other.getEmployee().isAvailable(e.getShift().getDay(), e.getShift().getHour())) {

            if (isContiguous(e.getEmployee(), e.getShift()) || isContiguous(other.getEmployee(), dayHourToShift(e.getShift().getDay(), e.getShift().getHour()))) {
              swapped = true;
              swap(e, other);

            }

          }

        }

      }
    }

  }

  private void swap(IssueEmployee e1, IssueEmployee e2) {
    if (!isScheduled(e1.getEmployee(), e1.getShift()) || !isScheduled(e2.getEmployee(), e2.getShift())) {
      return;
    }

    if (isScheduled(e1.getEmployee(), e2.getShift()) || isScheduled(e2.getEmployee(), e1.getShift())) {
      return;
    }

    if ((!possibleHasRequiredSkill(e2.getEmployee(), e1.getEmployee(), e1.getShift()) && hasRequiredSkill(e1.getShift()))
        || (!possibleHasRequiredSkill(e1.getEmployee(), e2.getEmployee(), e2.getShift()) && hasRequiredSkill(e2.getShift()))) {
      return;
    }

    if ((!possibleHasGenderBalance(e2.getEmployee(), e1.getEmployee(), e1.getShift()) && hasGenderBalance(e1.getShift()))
        || (!possibleHasGenderBalance(e1.getEmployee(), e2.getEmployee(), e2.getShift()) && hasGenderBalance(e2.getShift()))) {
      return;
    }

    e1.getShift().remove(e1.getEmployee());
    e2.getShift().remove(e2.getEmployee());

    e1.getShift().add(e2.getEmployee());
    e2.getShift().add(e1.getEmployee());
  }

  private Shift dayHourToShift(int day, int hour) {
    return _week.getShift(day, hour);
  }

  private void testSchedule() {
    // System.out.println("TEST SCHEDULE");
    for (int i = 0; i < _shifts.size(); i++) {
      if (_shifts.get(i).getDay() == 5 && _shifts.get(i).getHour() == 21)
        System.out.println("found " + _shifts.get(i).getDay() + "    " + _shifts.get(i).getHour());
    }
  }

  public SchedulingAlgo copy() {
    return new FXAlgo();
  }

}
