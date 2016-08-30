package comp110.youngjt;

import java.util.Random;

import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;
import comp110.Employee;

public class WhoIsAvailable implements SchedulingAlgo {
  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new WhoIsAvailable());
    String scenario = "week1";
    int trials = 1;
    bot.run(scenario, trials);
  }
  
  public Schedule run(Schedule schedule, Random random) {
    Week week = schedule.getWeek();
    Staff staff = schedule.getStaff();
    Shift[][] shifts = week.getShifts();
    
    getWhoIsAvailable(staff, 3, 13);
    
    return schedule;
  }

  private void getWhoIsAvailable(Staff staff, int day, int hour) {
    System.out.println("Day: " + day + " Hour: " + hour);
    for (Employee e : staff){
      if (e.isAvailable(day, hour)){
        System.out.println(e.getName());
      }
    }
  }
}
