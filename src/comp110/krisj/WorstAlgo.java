package comp110.krisj;

import java.util.Random;

import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Employee;
import comp110.KarenBot;
import comp110.Week;

public class WorstAlgo implements SchedulingAlgo {

  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new WorstAlgo());
    String scenario = "real-world-approx";
    int trials = 1000;
    bot.run(scenario, trials);
  }

  @Override
  public Schedule run(Schedule schedule, Random random) {
    Week week = schedule.getWeek();
    Shift[] buckets = this.weekAsFlatArray(week);

    Staff staff = schedule.getStaff();
    for (Employee employee : staff) {
      for (int i = 0; i < employee.getCapacity(); i++) {
        int attempts = 0; // Don't loop forever if it won't work
        while (attempts < 1000) {
          int randomIndex = random.nextInt(buckets.length);
          Shift shift = buckets[randomIndex];
          if (employee.isAvailable(shift.getDay(), shift.getHour()) && shift.contains(employee) == false) {
            buckets[randomIndex].add(employee);
            break;
          }
          attempts++;
        }
      }
    }

    return schedule;
  }

  private Shift[] weekAsFlatArray(Week week) {
    Shift[] buckets = new Shift[week.getNumberOfShifts()];
    int i = 0;
    for (Shift[] day : week.getShifts()) {
      for (Shift shift : day) {
        if (shift.getCapacity() > 0) {
          buckets[i++] = shift;
        }
      }
    }
    return buckets;
  }

}