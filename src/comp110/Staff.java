package comp110;

import java.util.HashSet;

public class Staff extends HashSet<Employee> {

  private static final long serialVersionUID = -5759157599215444115L;

  public double getCapacity() {
    double capacity = 0.0;
    for (Employee e : this) {
      capacity += (double) e.getCapacity();
    }
    return capacity;
  }

}
