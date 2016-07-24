package comp110.kscheng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

public class TheOriginalKarenBot implements SchedulingAlgo{
  
  private Staff staff;
  private Week week;
  private ArrayList<Shift> shifts;
  private ArrayList<Employee> shuffledStaff;
  
  public static void main(String[] args){
    
    KarenBot bot = new KarenBot(new TheOriginalKarenBot());
    bot.run("real-world-approx-two-hour-chunks", 1000);
  }

  @Override
  public Schedule run(Schedule schedule, Random random) {
    
    week = schedule.getWeek();
    staff = schedule.getStaff();
    shuffledStaff = this.staffAsArraylist(staff);
    Collections.shuffle(shuffledStaff, random);
    
    shifts = weekAsArray(week);
    this.fillShifts();
    
    return schedule;
  }
  
  public ArrayList<Employee> staffAsArraylist(Staff staff) {
    Employee[] employees = new Employee[0];
    employees = staff.toArray(employees);
    return new ArrayList<>(Arrays.asList(employees));
  }
  
  private ArrayList<Shift> weekAsArray(Week week) {
    ArrayList<Shift> buckets = new ArrayList<Shift>();
    for (Shift[] day : week.getShifts()) {
      for (Shift shift : day) {
        if (shift.getCapacity() > 0) {
          buckets.add(shift);
        }
      }
    }
    return buckets;
  }
  
  public void fillShifts(){
    
    for(Shift shift: shifts){
      for(Employee employee: shuffledStaff){
        if (shift.getCapacityRemaining() == 0) {
          break;
        } else {
          if (employee.getCapacityRemaining() > 0 && employee.isAvailable(shift.getDay(), shift.getHour())) {
            shift.add(employee);
          }
        }  
      }
    }
  }

}
