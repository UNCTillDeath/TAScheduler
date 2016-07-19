package comp110.youngjt;

import java.util.ArrayList;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

public class EvenWorseAlgo implements SchedulingAlgo {

  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new EvenWorseAlgo());
    String scenario = "hello-universe-contiguous";
    int trials = 1000;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule input, Random random) {
    Week week = input.getWeek();
    Staff staff = input.getStaff();

    // some really dumb stuff, TLDR: basically I need the employees in a native array
    ArrayList<Employee> temp = new ArrayList<Employee>();
    for (Employee employee : staff) {
      temp.add(employee);
    }
    Employee[] employees = new Employee[temp.size()];
    employees = temp.toArray(employees);
    // end dumb stuff

    Shift[][] shifts = week.getShifts();

    for (int day = 0; day < shifts.length; day++) {
      for (int hour = 0; hour < shifts[0].length; hour++) {
        boolean foundFirstEmployee = false;
        boolean firstEmployeeIsFemale = false;
        int firstEmployeeLevel = -1;
        boolean foundSecondEmployee = false;

        int iterations = 0;
        Shift shift = shifts[day][hour];
        ArrayList<Employee> scheduledEmployees = new ArrayList<Employee>();
        // iterate over each spot in each shift while it still has capacity
        while (shift.getCapacityRemaining() > 0) {
          // first iteration we don't care who we add as long as they are
          // available
          if (!foundFirstEmployee) {
            Employee firstEmployee = employees[random.nextInt(employees.length)];

            if (firstEmployee.isAvailable(day, hour) && firstEmployee.getCapacityRemaining() > 0) {
              shift.add(firstEmployee);
              scheduledEmployees.add(firstEmployee);
              foundFirstEmployee = true;
              firstEmployeeIsFemale = firstEmployee.getIsFemale();
              firstEmployeeLevel = firstEmployee.getLevel();
              scheduleContiguously(firstEmployee, day, hour, shifts, random);
            }
          }
          //for second employee we want to make sure they are different gender and diff skill
          else if (foundFirstEmployee){
            Employee secondEmployee = employees[random.nextInt(employees.length)];
            //after 990 we don't care, just put anyone there
            if ((secondEmployee.isAvailable(day, hour) && secondEmployee.getCapacityRemaining() > 0
                && !shift.contains(secondEmployee) && secondEmployee.getIsFemale() != firstEmployeeIsFemale && (secondEmployee.getLevel() >= getAverageSkill(scheduledEmployees))) || (iterations > 990 && secondEmployee.isAvailable(day, hour))) {
              shift.add(secondEmployee);
              scheduledEmployees.add(secondEmployee);
              foundSecondEmployee = true;
              scheduleContiguously(secondEmployee, day, hour, shifts, random);
            }
          }
          // filling the rest of the shifts capacity, after second we don't care about gender
          else if (foundFirstEmployee && foundSecondEmployee) {
            Employee nextEmployee = employees[random.nextInt(employees.length)];

            if ((nextEmployee.isAvailable(day, hour) && nextEmployee.getCapacityRemaining() > 0
                && !shift.contains(nextEmployee) && (nextEmployee.getLevel() >= getAverageSkill(scheduledEmployees))) || (iterations > 990 && nextEmployee.isAvailable(day, hour))) {

              shift.add(nextEmployee);
              scheduledEmployees.add(nextEmployee);
              scheduleContiguously(nextEmployee, day, hour, shifts, random);
            }
          }
          //prevent infinite loops, if we cant fill the schedule then eventually give up and move on
          if (iterations >= 1000){
            break;
          }
          iterations++;
        }
      }          
    }
    
    
    /* POST-PROCESS - ensure that all staff is fully utilized*/
    
    //do this several times because we may not fill all capacity in just one iteration
    for (int i = 0; i < 10; i++){
      if (staff.getRemainingCapacity() > 0){
        //loop over all employees with remaining capacity
        for (Employee employee : employees){
          if (employee.getCapacityRemaining() > 0){
            //loop over each shift with remaining capacity
            for (int day = 0; day < shifts.length; day++){
              for (int hour = 0; hour < shifts[0].length; hour++){
                Shift shift = shifts[day][hour];
                if (shift.getCapacity() > 0){
                  //we can be clever and use our contiguous scheduling logic by checking if and employee has been scheduled for a given time
                  if (shift.contains(employee)){
                    scheduleContiguouslyPostProcess(employee, day, hour, shifts, random);
                  }
                }  
              }
            }
          }
        }
      }
    }
    
    //if we cant schedule contiguously with an already scheduled shift, try scheduling contiguously anywhere
    for (int i = 0; i < 10; i++){
      if (staff.getRemainingCapacity() > 0){
        //loop over all employees with remaining capacity
        for (Employee employee : employees){
          if (employee.getCapacityRemaining() > 0){
            //loop over each shift with remaining capacity
            for (int day = 0; day < shifts.length; day++){
              for (int hour = 0; hour < shifts[0].length; hour++){
                Shift shift = shifts[day][hour];
                if (shift.getCapacity() > 0){
                    scheduleContiguouslyPostProcess(employee, day, hour, shifts, random);
                }  
              }
            }
          }
        }
      }
    }
    //schedules anywhere if we can't schedule contiguously
    for (int i = 0; i < 10; i++){
      if (staff.getRemainingCapacity() > 0){
        //loop over all employees with remaining capacity
        for (Employee employee : employees){
          if (employee.getCapacityRemaining() > 0){
            //loop over each shift with remaining capacity
            for (int day = 0; day < shifts.length; day++){
              for (int hour = 0; hour < shifts[0].length; hour++){
                Shift shift = shifts[day][hour];
                if (shift.getCapacity() > 0){
                  if(employee.isAvailable(day, hour) && employee.getCapacityRemaining() > 0
                      && !shift.contains(employee)){
                    shift.add(employee);
                  }
                }  
              }
            }
          }
        }
      }
    }
    
    /* POST-PROCESS - ensure that we have skill level > 1.5 */
    for (int i = 0; i < 10; i++) {
      // loop over each shift
      for (int day = 0; day < shifts.length; day++) {
        for (int hour = 0; hour < shifts[0].length; hour++) {
          // getting all employees scheduled during a particular shift
          ArrayList<Employee> employeesScheduledDuringShift = new ArrayList<Employee>();
          Shift shift = shifts[day][hour];
          for (Employee e : shift) {
            employeesScheduledDuringShift.add(e);
          }
          // check average skill
          if (getAverageSkill(employeesScheduledDuringShift) < 1.5) {
            Employee lowSkill = findLowestSkillEmployee(employeesScheduledDuringShift);
            //if nobody was scheduled previously for this shift
            if (lowSkill.getName().equals("dummy")){
              break;
            }
            shift.remove(lowSkill);
            //find someone higher skill
            boolean foundReplacement = false;
            for (Employee newEmployee : employees){
              if (newEmployee.isAvailable(day, hour) && newEmployee.getCapacityRemaining() > 0
                  && !shift.contains(newEmployee) && newEmployee.getLevel() >= 2  && hasSomeoneScheduledOfOppositeGender(newEmployee, shift)){
                shift.add(newEmployee);
                //scheduling them contiguously would also be nice
                scheduleContiguouslyPostProcess(newEmployee, day, hour, shifts, random);
                foundReplacement = true;
              }
            }
            //if we cannot find suitable replacement then just re-add old person
            if (!foundReplacement){
              shift.add(lowSkill);
            }
            //if we were able to replace find another shift that person was scheduled for and try to schedule them contiguosuly from there
            else {
              for (int day2 = 0; day2 < shifts.length; day2++){
                for (int hour2 = 0; hour2 < shifts[0].length; hour2++){
                  ArrayList<Employee> employeesScheduledDuringShiftReschedule = new ArrayList<Employee>();
                  Shift rescheduledShift = shifts[day2][hour2];
                  for (Employee e : rescheduledShift) {
                    employeesScheduledDuringShiftReschedule.add(e);
                  }
                  //we need to check capacity as well since to prevent this if from executing multiple times
                  if (!rescheduledShift.contains(lowSkill) && lowSkill.getCapacity() > 0 && getAverageSkill(employeesScheduledDuringShiftReschedule) > 1.5 && hasSomeoneScheduledOfOppositeGender(lowSkill, rescheduledShift)){
                    rescheduledShift.add(lowSkill);
                    scheduleContiguouslyPostProcess(lowSkill, day, hour, shifts, random);
                  }
                }
              }
            }
          }
        }
      }
    }


    return input;
  }
  
  //basically finding min
  private Employee
      findLowestSkillEmployee(ArrayList<Employee> employees) {
    if (employees.size() == 0){
       return new Employee("dummy", 0, false, -1, null);
    }
    Employee min = employees.get(0);
    for (Employee e : employees) {
      if (e.getLevel() < min.getLevel()) {
        ///new min found
        min = e;
      }
    }
    return min;
  }

  //identical to other contiguous method except we are a little more relaxed on requirements needed to schedule (no skill check)
  private boolean scheduleContiguouslyPostProcess(Employee employee, int startDay, int startHour,
      Shift[][] shifts, Random rand) {
    boolean successful = false;
    int numberOfHoursToSchedule = Math.min((23 - startHour) , generateRandomInt(1, 3, rand));
    
    for (int i = 0; i < numberOfHoursToSchedule; i++){
      // getting employees that are scheduled for the current shift
      ArrayList<Employee> employeesScheduledDuringShift = new ArrayList<Employee>();
      Shift currentShift = shifts[startDay][startHour + i + 1];
      for (Employee e : currentShift) {
        employeesScheduledDuringShift.add(e);
      }

      if(employee.isAvailable(startDay, startHour + i + 1) && employee.getCapacityRemaining() > 0
          && !currentShift.contains(employee) && hasSomeoneScheduledOfOppositeGender(employee, currentShift)){
        currentShift.add(employee);
        successful = true;
      }
    }
    return successful;
  }

  private double getAverageSkill(ArrayList<Employee> employees){
    //no employees have been scheduled for this shift yet
    if (employees.size() == 0){
      return 0;
    }
    int totalLevel = 0;
    for (Employee e : employees){
      totalLevel += e.getLevel();
    }
    return totalLevel / employees.size();
  }
  
  //assume employee has already been scheduled for start hour, thats why everything is +1 offset
  private void scheduleContiguously(Employee employee, int startDay, int startHour, Shift[][] shifts, Random rand){
    int numberOfHoursToSchedule = Math.min((23 - startHour) , generateRandomInt(1, 3, rand));
    
    for (int i = 0; i < numberOfHoursToSchedule; i++){
      // getting employees that are scheduled for the current shift
      ArrayList<Employee> employeesScheduledDuringShift = new ArrayList<Employee>();
      Shift currentShift = shifts[startDay][startHour + i + 1];
      for (Employee e : currentShift) {
        employeesScheduledDuringShift.add(e);
      }

      if(employee.isAvailable(startDay, startHour + i + 1) && employee.getCapacityRemaining() > 0
          && !currentShift.contains(employee) && (employee.getLevel() >= getAverageSkill(employeesScheduledDuringShift)) && hasSomeoneScheduledOfOppositeGender(employee, currentShift)){
        currentShift.add(employee);
      }
    }
  }
  
  /*
   * Helper method that generates a random number between min and max
   * (inclusive)
   */
  private static int generateRandomInt(int min, int max, Random rand) {
    return rand.nextInt((max - min) + 1) + min;
  }
  
  private boolean hasSomeoneScheduledOfOppositeGender(Employee employee, Shift shift){
    //if nobody is scheduled then this is irrelevant
    if (shift.size() == 0){
      return true;
    }
    for (Employee scheduledEmployee : shift){
      if (scheduledEmployee.getIsFemale() != employee.getIsFemale()){
        return true;
      }
    }
    return false;
  }

}
