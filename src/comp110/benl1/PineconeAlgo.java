package comp110.benl1;

import java.util.ArrayList;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

/**
 * Essentially the method here is pick random people to fill up all the slots, then when there is one person left for a shift
 * try to find someone that rounds it out
 */
public class PineconeAlgo implements SchedulingAlgo {

  private Schedule _schedule;

  private Week _week;

  private Staff _staff;

  private Shift[][] _shifts;

  private ArrayList<Employee> _employees;

  private Random _random;

  private int _genderAttempt;

  private int _skillAttempt;

  private int _shiftAttempt;
  
  private int _genderAndSkillAttempt;
  
  private static final int SHIFT_FILL = 2000;
  
  private static final int LOOK_FOR_GENDER = 100;
  
  private static final int LOOK_FOR_SKILL = 250;
  
  private static final int LOOK_FOR_GENDER_AND_SKILL = 75;
  
  private static final int CONTIGUOUS_SLOTS = 4;
  
  private static final int LATEST_SHIFT = 23;

  @Override
  public Schedule run(Schedule input, Random random) {

    setup(input, random);

    assignSchedule();
    
    testSchedule();

    return input;
  }

  private void setup(Schedule input, Random random) {

    _week = input.getWeek();

    _staff = input.getStaff();

    _employees = new ArrayList<Employee>();
    for (Employee e : _staff) {
      _employees.add(e);
    }

    _shifts = _week.getShifts();

    _random = random;

  }

  private void assignSchedule() {

    //Loop to go through all shifts
    for (int i = 0; i < _shifts.length; i++) {
      for (int j = 0; j < _shifts[0].length; j++) {

        //reset shift/gender/skill attempt variables
        _genderAttempt = 0;
        _skillAttempt = 0;
        _shiftAttempt = 0;
        _genderAndSkillAttempt = 0;

        //Loop meanwhile the shift still needs to be filled
        while (_shifts[i][j].getCapacityRemaining() > 0) {

          //If passed max shift attempts, move onto next shift
          if (_shiftAttempt > SHIFT_FILL) {
            break;
          }

          _shiftAttempt++;
          
          //check to see if all employees are at full capacity
          if (_employees.size() == 0) continue;

          //Random employee index
          int EI = _random.nextInt(_employees.size());

          //Hold reference to employee
          Employee toAdd = _employees.get(EI);

          if (scheduleEmployee(toAdd, i, j)) {
            attemptContiguousShift(toAdd, i, j);
          }
        }

      }
    }

  }

  //returns true if successful, false for failurie
  private boolean scheduleEmployee(Employee e, int day, int hour) {
    
    //if the shift is already full don't try to schedule anyone
    if (_shifts[day][hour].getCapacityRemaining() == 0) {
      return false;
    }

    //check to see if the employee is available at that time and is below their hour capacity
    if (e.isAvailable(day, hour) && e.getCapacityRemaining() > 0) {

      //worry about gender/skill if there is only one more person to be put in the shift
      if (_shifts[day][hour].getCapacityRemaining() == 1) {
        
        //for first fifty tries, attempt to find someone to fill both gender and skill
        if ((!possibleHasGenderBalance(e, day, hour) || !possibleHasRequiredSkill(e, day, hour)) && 
            _genderAndSkillAttempt < LOOK_FOR_GENDER_AND_SKILL){
          _genderAndSkillAttempt++;
          return false;
        }
        
        
        //If no gender equality and not over max attempts to find it
        if (!possibleHasGenderBalance(e, day, hour) && _genderAttempt < LOOK_FOR_GENDER) {
          _genderAttempt++;
          return false;
        }
        
        
        
        //If below required skill and not over max attempts to find it
        if (!possibleHasRequiredSkill(e, day, hour) && _skillAttempt < LOOK_FOR_SKILL && !possibleHasGenderBalance(e, day, hour)) {
          _skillAttempt++;
          return false;
        }

      }
      boolean success = _shifts[day][hour].add(e);
      
      //If that was the last hour for the employee, remove them from list of possible employees
      if (e.getCapacityRemaining() == 0) {
        _employees.remove(e);
      }
      
      return success;

    }
    
    return false;
  }
  
  //This method will be called if an employee is successfully scheduled for a time slot and will attempt to give them contiguous shifts
  private void attemptContiguousShift(Employee e, int day, int hour){
    
    for (int i = 1; i < CONTIGUOUS_SLOTS; i++){
    
      //return if this shift is passed the latest shift
      if ((hour + i) > LATEST_SHIFT) return;
      
      //try to schedule the employee for the contiguous shift
      if (!scheduleEmployee(e, day, hour + i)){
        
        //if unsuccessful in scheduling for next shift, don't bother continuing
        return;
      }
    }
    
    
  }

  //checks if a shift has gender balance with a certain employee added
  private boolean possibleHasGenderBalance(Employee toAdd, int day, int hour) {

    //Can't have gender equality with 1 person
    if (_shifts[day][hour].getCapacity() == 1) return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : _shifts[day][hour]) {
      if (e.getIsFemale()) hasFemale = true;
      if (!e.getIsFemale()) hasMale = true;
    }
    
    if (toAdd.getIsFemale()) hasFemale = true;
    else hasMale = true;

    return hasMale && hasFemale;
  }
  
  private boolean hasGenderBalance(int day, int hour){
    //Can't have gender equality with 1 person
    if (_shifts[day][hour].getCapacity() == 1) return true;

    boolean hasMale = false;
    boolean hasFemale = false;

    for (Employee e : _shifts[day][hour]) {
      if (e.getIsFemale()) hasFemale = true;
      if (!e.getIsFemale()) hasMale = true;
    }

    return hasMale && hasFemale;
  }

  //Theoretically adds the employee to the shift to test if the level is > 1.5
  private boolean possibleHasRequiredSkill(Employee e, int day, int hour) {

    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee toAdd : _shifts[day][hour]) {
      totalSkill += toAdd.getLevel();
      numOfEmployees++;
    }

    //"add" employee
    totalSkill += e.getLevel();
    numOfEmployees++;

    return (totalSkill / numOfEmployees) >= 1.5;
  }
  
  private boolean hasRequiredSkill(int day, int hour) {

    double totalSkill = 0.0;
    double numOfEmployees = 0.0;

    for (Employee toAdd : _shifts[day][hour]) {
      totalSkill += toAdd.getLevel();
      numOfEmployees++;
    }

    return (totalSkill / numOfEmployees) >= 1.5;
  }
  
  
  //Method to test issues with schedule before returning it
  private void testSchedule(){
    for (int i = 0; i < _shifts.length; i++){
      for (int j = 0; j < _shifts[0].length; j++){
        if (_shifts[i][j].size() > _shifts[i][j].getCapacity()) {
          System.out.println("not again please");
        }
      }
    }
  }

  
  //Run through every possible switch and see if any will help
  private void attemptFix(int day, int hour){
    //go through all shifts
    for (int i = 0; i < _shifts.length; i++){
      for (int j = 0; j < _shifts[0].length; j++){
        
        
        
      }
    }
    
    
  }

  public static void main(String[] args) {

    KarenBot karenBot = new KarenBot(new PineconeAlgo());

    karenBot.run("real-world-approx-two-hour-chunks", 1000);
  }

}
