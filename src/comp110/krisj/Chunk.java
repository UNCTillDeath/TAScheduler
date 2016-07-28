package comp110.krisj;

import comp110.Employee;

public class Chunk {

  int _day, _start, _end;

  Employee _employee;

  public Chunk(Employee employee, int day, int start, int end) {
    _day = day;
    _start = start;
    _end = end;
    _employee = employee;
  }

  public int getDay() {
    return _day;
  }

  public int getStart() {
    return _start;
  }

  public int getEnd() {
    return _end;
  }

  public boolean intersects(int day, int hour) {
    return day == _day && hour >= _start && hour <= _end;
  }

  public Employee getEmployee() {
    return _employee;
  }

}