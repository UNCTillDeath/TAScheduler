package comp110.FXCrew;

import java.util.ArrayList;
import java.util.List;

import comp110.Employee;
import comp110.Shift;

public class Chunk {

  private Employee    _employee;
  private int         _day;
  private int         _startHour;
  private int         _endHour;
  private List<Shift> _shifts;

  Chunk(Employee employee, Shift shift) {
    _employee = employee;
    _day = shift.getDay();
    _startHour = shift.getHour();
    _endHour = shift.getHour();
    _shifts = new ArrayList<>();
    _shifts.add(shift);
  }

  public void addShift(Shift shift) {
    _shifts.add(shift);
    if (shift.getHour() > _endHour)
      _endHour = shift.getHour();
    if (shift.getHour() < _startHour)
      _startHour = shift.getHour();
  }

  public boolean scheduleEmployeeToChunk() {
    boolean success = true;
    for (Shift s : _shifts) {
      if (_employee.getCapacityRemaining() > 0 && s.getCapacityRemaining() > 0) {
        if (!s.add(_employee)) {
          success = false;
        }
      }
    }
    return success;
  }

  @Override
  public String toString() {
    return "Day: " + _day + " " + _startHour + " - " + _endHour;
  }

  public List<Shift> getShifts() {
    return _shifts;
  }

  public Employee getEmployee() {
    return _employee;
  }

  public int getDay() {
    return _day;
  }

  public int getStartHour() {
    return _startHour;
  }

  public int getEndHour() {
    return _endHour;
  }

  public int getSize() {
    return _endHour - _startHour;
  }

  public boolean shiftInChunk(int day, int hour) {

    if (day != _day)
      return false;
    if (hour < _startHour || hour > _endHour)
      return false;

    return true;
  }

  public boolean shiftInChunk(Shift shift) {
    return shiftInChunk(shift.getDay(), shift.getHour());
  }

}
