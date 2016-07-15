package comp110;

public class Schedule {

  private Staff _staff;
  private Week _week;

  public Schedule(Staff staff, Week week) {
    _staff = staff;
    _week = week;
  }

  public Staff getStaff() {
    return _staff;
  }

  public Week getWeek() {
    return _week;
  }

  public boolean equals(Schedule other) {
    return _staff.equals(other._staff) && _week.equals(other._week);
  }

  public Schedule copy() {
    return new Schedule(_staff.copy(), _week.copy());
  }

}
