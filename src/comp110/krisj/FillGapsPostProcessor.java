package comp110.krisj;

import comp110.Employee;
import comp110.Schedule;
import comp110.Shift;

public class FillGapsPostProcessor implements PostProcessor {

  @Override
  public void process(Schedule schedule) {
    Shift[][] week = schedule.getWeek().getShifts();
    for (int day = 0; day < week.length; day++) {
      for (int hour = 0; hour < week[day].length; hour++) {
        for (Employee employee : schedule.getStaff()) {
          if (week[day][hour].getCapacityRemaining() > 0) {
            if (employee.getCapacityRemaining() > 0 && employee.isAvailable(day, hour)) {
              week[day][hour].add(employee);
            }
          } else {
            break;
          }
        }
      }
    }
  }

}
