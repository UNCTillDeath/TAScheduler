package comp110.youngjt;

import comp110.Employee;

public class Chunk {

  private Employee _employee;
  private int      _day;
  private int      _startHour;
  private int      _endHour;

  Chunk(Employee employee, int day, int startHour, int endHour) {
    _employee = employee;
    _day = day;
    _startHour = startHour;
    _endHour = endHour;
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

}
