package comp110;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Shift extends HashSet<Employee> {

  /* Even though we don't serialize, this keeps Java from complaining... */
  private static final long serialVersionUID = 5723878473617645106L;

  private int _hour;
  private int _capacity;

  public Shift(int hour, int capacity) {
    _hour = hour;
    _capacity = capacity;
  }

  public String toString() {
    List<String> names = this.stream().map(e -> e.getName()).collect(Collectors.toList());
    return String.format("%02d", _hour) + ": " + String.join(", ", names);
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

  public void setCapacity(int capacity) {
    _capacity = capacity;
  }

  public Shift copy() {
    Shift copy = new Shift(_hour, _capacity);
    Iterator<Employee> itr = this.iterator();
    while (itr.hasNext()) {
      copy.add(itr.next().copy());
    }
    return copy;
  }

}
