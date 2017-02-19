package comp110;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Shift extends HashSet<Employee> implements Serializable {

  /* Even though we don't serialize, this keeps Java from complaining... */
  private static final long serialVersionUID = 5723878473617645106L;

  private int               _day;
  private int               _hour;
  private int               _capacity;

  public Shift(int day, int hour, int capacity) {
    _day = day;
    _hour = hour;
    _capacity = capacity;
  }

  public boolean add(Employee e) {
    boolean added = super.add(e);
    if (added) {
      e.setCapacityUsed(e.getCapacityUsed() + 1);
    }
    return added;
  }

  public boolean remove(Employee e) {
    boolean removed = super.remove(e);
    if (removed) {
      e.setCapacityUsed(e.getCapacityUsed() - 1);
    }
    return removed;
  }

  public String toString() {
    List<String> names = this.stream().map(e -> e.getName()).collect(Collectors.toList());
    return String.format("%02d", _hour) + ": (" + String.format("%02d", names.size()) + ") " + String.join(", ", names);
  }

  public boolean equals(Shift other) {
    boolean equals = _day == other._day;
    equals = equals && _hour == other._hour;
    equals = equals && _capacity == other._capacity;

    if (equals) {
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
    }

    equals = equals && this.size() == other.size();
    return equals;
  }

  public int getDay() {
    return _day;
  }

  public void setDay(int day) {
    _day = day;
  }

  public int getHour() {
    return _hour;
  }

  public void setHour(int hour) {
    _hour = hour;
  }

  public int getCapacity() {
    return _capacity;
  }

  public int getCapacityRemaining() {
    return _capacity - size();
  }

  public Shift copy() {
    Shift copy = new Shift(_day, _hour, _capacity);
    Iterator<Employee> itr = this.iterator();
    while (itr.hasNext()) {
      copy.add(itr.next().copy());
    }
    return copy;
  }

}
