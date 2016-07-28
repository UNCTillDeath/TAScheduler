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

  public double getRemainingCapacity() {
    double remainingCapacity = 0;
    for (Employee e : this) {
      remainingCapacity += (double) e.getCapacityRemaining();
    }
    return remainingCapacity;
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
    // This is literal vomit. Someone please figure out and teach me why Set's
    // containsAll method fails here. This should be O(n) not O(n^2). Ugh.
    for (Employee e : this) {
      boolean contains = false;
      for (Employee o : other) {
        if (e.equals(o)) {
          contains = true;
          break;
        }
      }
      if (contains == false) {
        return false;
      }
    }
    return this.size() == other.size();
  }

  public Employee getEmployeeByName(String name) {
    for (Employee e : this) {
      if (e.getName().equals(name)) {
        return e;
      }
    }
    return null;
  }

}
