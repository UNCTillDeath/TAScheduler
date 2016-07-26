package comp110.FXCrew;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import comp110.Employee;
import comp110.Schedule;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;

// The purpose of this class is to order all shifts by how difficult they are to fill.
// On a first pass, I'm not going to pay attention to gender, expertise, nor contiguous.

// The intended use of this class is to allow a scheduling algorithm to begin by
// attempting to fill the most constrained shifts first.

public class ShiftsByStaffConstraints {

  private Week _week;
  private Shift[][] _shifts;
  private List<Shift> _sortedShifts;
  private Staff _staff;
  private Schedule _schedule;

  public ShiftsByStaffConstraints() {
    _week = new Week("ShiftsByStaff");
    _shifts = _week.getShifts();
    _sortedShifts = new ArrayList<Shift>();
  }

  public void loadSchedule(Schedule schedule) {

    _schedule = schedule;
    _staff = _schedule.getStaff();

    Staff staff = schedule.getStaff();
    for (Employee employee : staff) {
      int[][] availability = employee.getAvailability();
      for (int day = 0; day < availability.length; day++) {
        int[] hours = availability[day];
        for (int hour = 0; hour < hours.length; hour++) {
          // A shift's capacity is not a field we can mutate. It is fixed at
          // construction. We are going to construct a new shift, with capacity
          // incremented by 1, each time.
          if (employee.isAvailable(day, hour) && schedule.getWeek().getShift(day, hour).getCapacityRemaining() > 0) {
            _shifts[day][hour] = new Shift(day, hour, _shifts[day][hour].getCapacity() + 1);
          }
        }
      }
    }

    // At this point each shift's capacity is the total # of employees who are
    // available for that shift. Now we're going to flatten the 2D array into a
    // list so that we can sort the list by # of employees available ascending.
    List<Shift> shifts1D = new ArrayList<Shift>();
    for (int day = 0; day < _shifts.length; day++) {
      for (int hour = 0; hour < _shifts[day].length; hour++) {
        shifts1D.add(_shifts[day][hour]);
      }
    }

    // Finally, we'll sort the shifts by their capacity. I'm going to use some
    // stream API glory here. Embrace the power of lambdas.
    Stream<Shift> shiftsStream = shifts1D.stream();
    Stream<Shift> filteredStream = shiftsStream.filter((s) -> s.getCapacity() > 0);
    //if a is greater than b return 1
    //a and b are shifts
    Stream<Shift> sortedStream = filteredStream.sorted((a, b) -> {
      
      double aScore = 0.0;
      double bScore = 0.0;

      int aAvailable = 0;
      int bAvailable = 0;
      
      ArrayList<Employee> aAvailableEmployees = new ArrayList<Employee>();
      ArrayList<Employee> bAvailableEmployees = new ArrayList<Employee>();

      int aCapacity = a.getCapacity();
      int bCapacity = b.getCapacity();

      int aMalesAvailable = 0;
      int bMalesAvailable = 0;
      
      double aAvailableSkillAvg, bAvailableSkillAvg;
      
      double aTotalSkill = 0.0;
      double bTotalSkill = 0.0;

      double aGenderRatio, bGenderRatio;

      double aAvailableRatio, bAvailableRatio;

      //counts people available
      for (Employee e : _staff) {

        if (e.isAvailable(a.getDay(), a.getHour())) {
          aAvailable++;
          aAvailableEmployees.add(e);
          if (!e.getIsFemale()) {
            aMalesAvailable++;
          }
        }

        if (e.isAvailable(b.getDay(), b.getHour())) {
          bAvailable++;
          bAvailableEmployees.add(e);
          if (!e.getIsFemale()) {
            bMalesAvailable++;
          }
        }
        
      }
      
      for (Employee e : aAvailableEmployees){
        aTotalSkill += e.getLevel();
      }
      
      for (Employee e : bAvailableEmployees){
        bTotalSkill += e.getLevel();
      }
      
      aAvailableSkillAvg = aTotalSkill / (double) aAvailableEmployees.size();
      
      bAvailableSkillAvg = bTotalSkill / (double) bAvailableEmployees.size();
      //if more males than females, do ratio m:f else ratio f:m 
      if (aCapacity != aMalesAvailable && aMalesAvailable >= (aCapacity - aMalesAvailable)) {
        aGenderRatio = (double) aMalesAvailable / (double) (aCapacity - aMalesAvailable);
      }
      else{
        aGenderRatio = (double) (aCapacity - aMalesAvailable) / (double) aMalesAvailable;
      }
      
      if (bCapacity != bMalesAvailable && bMalesAvailable >= (bCapacity - bMalesAvailable)) {
        bGenderRatio = (double) bMalesAvailable / (double) (bCapacity - bMalesAvailable);
      }
      else{
        bGenderRatio = (double) (bCapacity - bMalesAvailable) / (double) aMalesAvailable;
      }

      aAvailableRatio = (double) aAvailable / (double) aCapacity;
      bAvailableRatio = (double) bAvailable / (double) bCapacity;
      
      aScore += aAvailableRatio;
      aScore += aAvailableSkillAvg;
      if (aGenderRatio > bGenderRatio){
        aScore++;
      }
      else {
        bScore++;
      }
      
      bScore += bAvailableRatio;
      bScore += bAvailableSkillAvg;

      if (aScore > bScore) {
        return -1;
      }
      else if (bScore > aScore) {
        return 1;
      }
      else {
        return 0;
      }

    });

    _sortedShifts = sortedStream.collect(Collectors.toList());
  }

  public List<Shift> getSortedShifts() {
    return _sortedShifts;
  }

}