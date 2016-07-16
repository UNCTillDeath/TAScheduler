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
    String scenario = "hello-world-gender";
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
        boolean foundSecondEmployee = false;

        // iterate over each spot in each shift while it still has capacity
        int iterations = 0;
        while (shifts[day][hour].getCapacityRemaining() > 0) {
          // first iteration we don't care who we add as long as they are
          // available
          if (!foundFirstEmployee) {
            Employee firstEmployee = employees[random.nextInt(employees.length)];

            if (firstEmployee.isAvailable(day, hour) && firstEmployee.getCapacityRemaining() > 0) {
              shifts[day][hour].add(firstEmployee);
              foundFirstEmployee = true;
              firstEmployeeIsFemale = firstEmployee.getIsFemale();
            }
          }
          //for second employee we want to make sure they are different gender
          if (foundFirstEmployee){
            Employee secondEmployee = employees[random.nextInt(employees.length)];
            if (secondEmployee.isAvailable(day, hour) && secondEmployee.getCapacityRemaining() > 0
                && !shifts[day][hour].contains(secondEmployee) && secondEmployee.getIsFemale() != firstEmployeeIsFemale) {
              shifts[day][hour].add(secondEmployee);
              foundSecondEmployee = true;
            }
          }
          // filling the rest of the shifts capacity, after second we don't care about gender
          if (foundFirstEmployee && foundSecondEmployee) {
            Employee nextEmployee = employees[random.nextInt(employees.length)];

            if (nextEmployee.isAvailable(day, hour) && nextEmployee.getCapacityRemaining() > 0
                && !shifts[day][hour].contains(nextEmployee)) {

              shifts[day][hour].add(nextEmployee);
            }
          }
          //prevent infinite loops, if we cant fill the schedule then eventually move on
          if (iterations >= 1000){
            continue;
          }
          iterations++;
        }
      }

    }

    return input;
  }

}
