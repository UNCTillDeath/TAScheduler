package comp110.sfirrin;

import comp110.Employee;

public class ScoredEmployee {

  private Employee _employee;
  
  private double _score;
  
  public ScoredEmployee(Employee employee, double score){
    _employee = employee;
    _score = score;
  }
  
  public double getScore(){
    return _score;
  }
  
  public Employee getEmployee(){
    return _employee;
  }
}
