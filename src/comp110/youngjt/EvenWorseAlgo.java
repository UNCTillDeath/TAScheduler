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
import comp110.krisj.WorstAlgo;

public class EvenWorseAlgo implements SchedulingAlgo {
  
  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new EvenWorseAlgo());
    String scenario = "hello-world";
    int trials = 10;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule input, Random random) {
    Week week = input.getWeek();
    Staff staff = input.getStaff();
    
    //some really dumb stuff
    ArrayList<Employee> temp = new ArrayList<Employee>();
    for (Employee employee : staff) {
      temp.add(employee);
    }
    Employee[] employees = new Employee[temp.size()];
    employees = temp.toArray(employees);
    //end dumb stuff
    
    Shift[][] shifts = week.getShifts();
    
    for (int day = 0; day < shifts.length; day++){
      for (int hour = 0; hour < shifts[0].length; hour++){
        boolean foundFirstEmployee = false;
        boolean firstEmployeeIsFemale = false;
        
        //iterate over each spot in each shift while it still has capacity
        while (shifts[day][hour].getCapacity() > 0){
          //System.out.println(shifts[day][hour].getCapacity());
          //first iteration we don't care who we add as long as they are available
          if (!foundFirstEmployee){
            Employee firstEmployee = employees[random.nextInt(employees.length)];
            
            if (firstEmployee.isAvailable(day, hour) && firstEmployee.getCapacity() > 0){
              shifts[day][hour].add(firstEmployee);
              
              //reduce capacity
              firstEmployee.setCapacity(firstEmployee.getCapacity() - 1);
              shifts[day][hour].setCapacity(shifts[day][hour].getCapacity() - 1);
              foundFirstEmployee = true;
              firstEmployeeIsFemale = firstEmployee.getIsFemale();
            }
          }
          //filling the rest of the shifts capacity
          if (foundFirstEmployee){
            Employee nextEmployee = employees[random.nextInt(employees.length)];
            //if (nextEmployee.isAvailable(day, hour) && nextEmployee.getIsFemale() != firstEmployeeIsFemale && nextEmployee.getCapacity() > 0 && !shifts[day][hour].contains(nextEmployee)){
            if (nextEmployee.isAvailable(day, hour) && nextEmployee.getCapacity() > 0 && !shifts[day][hour].contains(nextEmployee)){
  
              shifts[day][hour].add(nextEmployee);
              
              //reduce capacity
              nextEmployee.setCapacity(nextEmployee.getCapacity() - 1);
              shifts[day][hour].setCapacity(shifts[day][hour].getCapacity() - 1);
            }
          }
        }
      }

    }

    return input;
  }

}
