package comp110;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;

public class Scorecard extends ArrayList<Scoreline> {

  private static final long serialVersionUID = 1628749563422293562L;

  private Schedule          _schedule;

  public Scorecard(Schedule schedule) {
    _schedule = schedule;
  }

  public double getScore() {
    double score = 0;
    for (Scoreline line : this) {
      score = score + line.getValue();
    }
    return score;
  }

  public String toString() {
    String result = "";
    Iterator<Scoreline> itr = this.iterator();
    while (itr.hasNext()) {
      Scoreline scoreline = itr.next();
      Formatter f = new Formatter();
      result += f.format("%.3f - %s%n", scoreline.getValue(), scoreline.getLabel()).toString();
    }
    return result;
  }

  public String getDiagnostics() {
    String result = "";
    Iterator<Scoreline> itr = this.iterator();
    while (itr.hasNext()) {
      Scoreline scoreline = itr.next();
      if (scoreline.size() > 0) {
        result += scoreline.getLabel() + "\n";
        for (String issue : scoreline) {
          result += "\t" + issue + "\n";
        }
      }
    }
    return result;
  }

  public Schedule getSchedule() {
    return _schedule;
  }

}