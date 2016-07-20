package comp110.krisj;

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

  public ShiftsByStaffConstraints() {
    _week = new Week("ShiftsByStaff");
    _shifts = _week.getShifts();
    _sortedShifts = new ArrayList<Shift>();
  }

  public void loadSchedule(Schedule schedule) {

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
    Stream<Shift> sortedStream = filteredStream.sorted((a, b) -> {
      if (a.getCapacity() == b.getCapacity()) {
        return 0;
      } else {
        if (a.getCapacity() > b.getCapacity()) {
          return 1;
        } else {
          return -1;
        }
      }
    });
    _sortedShifts = sortedStream.collect(Collectors.toList());
  }

  public List<Shift> getSortedShifts() {
    return _sortedShifts;
  }

}