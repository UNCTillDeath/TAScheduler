package comp110;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Week extends HashMap<String, Shifts> {

  private static final long serialVersionUID = 6010261574305462796L;

  public double getScheduledHours() {
    double hours = 0.0;
    for (Shifts day : this.values()) {
      for (Shift shift : day) {
        hours += shift.size();
      }
    }
    return hours;
  }

  public int getNumberOfShifts() {
    int shifts = 0;
    for (Shifts day : this.values()) {
      shifts += day.size();
    }
    return shifts;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (String day : this.getDays()) {
      sb.append(day + "\n");
      for (Shift shift : this.get(day)) {
        sb.append("\t" + shift + "\n");
      }
    }

    return sb.toString();
  }

  private List<String> getDays() {
    return this.keySet().stream().sorted().collect(Collectors.toList());
  }

}
