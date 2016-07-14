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

}
