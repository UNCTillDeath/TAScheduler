package comp110.krisj;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;

/**
 * Another fairly naive algorithm. The idea I'm trying here is sorting shifts by
 * their capacity and filling the most constrained shifts first.
 * 
 * This currently barely outperforms SimpleRandomAlgo.
 */
public class ToolingAlgo implements SchedulingAlgo {

  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new ToolingAlgo());
    String scenario = "real-world-approx-two-hour-chunks";
    int trials = 1000;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule schedule, Random random) {
    ArrayList<Employee> staff = this.staffAsArraylist(schedule.getStaff());
    Collections.shuffle(staff, random);
    ShiftsByStaffConstraints shiftsConstrained = new ShiftsByStaffConstraints();
    shiftsConstrained.loadSchedule(schedule);
    for (Shift sortedShift : shiftsConstrained.getSortedShifts()) {
      Shift shift = schedule.getWeek().getShift(sortedShift.getDay(), sortedShift.getHour());
      for (Employee employee : staff) {
        if (shift.getCapacityRemaining() > 0) {
          if (employee.getCapacityRemaining() > 0) {
            if (employee.isAvailable(sortedShift.getDay(), sortedShift.getHour())) {
              shift.add(employee);
            }
          }
        } else {
          break;
        }
      }
    }

    return schedule;
  }

  public ArrayList<Employee> staffAsArraylist(Staff staff) {
    Employee[] employees = new Employee[0];
    employees = staff.toArray(employees);
    return new ArrayList<>(Arrays.asList(employees));
  }
}
