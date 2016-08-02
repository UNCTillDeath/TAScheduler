package comp110.FXCrew;

import comp110.Employee;
import comp110.Shift;

/**
 * 
 * Wrapper class for employees that are scheduled for a shift which is causing an issue
 * i.e scheduled for a 1 hour shift
 */
public class IssueEmployee {

  public Employee getEmployee() {
    return _employee;
  }

  public Shift getShift() {
    return _shift;
  }

  private Employee _employee;

  private Shift _shift;

  public IssueEmployee(Employee e, Shift shift) {
    _employee = e;
    _shift = shift;
  }
}
