package comp110;

import java.util.HashSet;
import java.util.Iterator;

public class Staff extends HashSet<Employee> {

  private static final long serialVersionUID = -5759157599215444115L;

  public double getCapacity() {
    double capacity = 0.0;
    for (Employee e : this) {
      capacity += (double) e.getCapacity();
    }
    return capacity;
  }

  public Staff copy() {
    Staff copy = new Staff();
    Iterator<Employee> itr = this.iterator();
    while (itr.hasNext()) {
      copy.add(itr.next().copy());
    }
    return copy;
  }

  public boolean equals(Staff other) {
    boolean equals = this.containsAll(other);
    equals = equals && this.size() == other.size();
    return equals;
  }

}
