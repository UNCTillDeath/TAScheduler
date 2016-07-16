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
    String scenario = "hello-world-experience";
    int trials = 10;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule input, Random random) {
    Week week = input.getWeek();
    Staff staff = input.getStaff();

    // some really dumb stuff
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
        // iterate over each spot in each shift while it still has capacity
        while (shift.getCapacityRemaining() > 0) {
          // first iteration we don't care who we add as long as they are
          // available
          if (!foundFirstEmployee) {
            Employee firstEmployee = employees[random.nextInt(employees.length)];

            if (firstEmployee.isAvailable(day, hour) && firstEmployee.getCapacityRemaining() > 0) {
              shift.add(firstEmployee);
              foundFirstEmployee = true;
              firstEmployeeIsFemale = firstEmployee.getIsFemale();
              firstEmployeeLevel = firstEmployee.getLevel();
            }
          }
          //for second employee we want to make sure they are different gender and diff skill
          else if (foundFirstEmployee){
            Employee secondEmployee = employees[random.nextInt(employees.length)];
            //after 990 we don't care, just put anyone there
            if ((secondEmployee.isAvailable(day, hour) && secondEmployee.getCapacityRemaining() > 0
                && !shift.contains(secondEmployee) && secondEmployee.getIsFemale() != firstEmployeeIsFemale && secondEmployee.getLevel() != firstEmployeeLevel) || iterations > 990) {
              shift.add(secondEmployee);
              foundSecondEmployee = true;
            }
          }
          // filling the rest of the shifts capacity, after second we don't care about gender
          else if (foundFirstEmployee && foundSecondEmployee) {
            Employee nextEmployee = employees[random.nextInt(employees.length)];

            if ((nextEmployee.isAvailable(day, hour) && nextEmployee.getCapacityRemaining() > 0
                && !shift.contains(nextEmployee)) || iterations > 990) {

              shift.add(nextEmployee);
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

    return input;
  }

}
