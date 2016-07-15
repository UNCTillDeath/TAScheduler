package comp110;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Iterator;

import com.sun.javafx.binding.StringFormatter;

public class Scorecard extends ArrayList<Scoreline> {

  private static final long serialVersionUID = 1628749563422293562L;

  Schedule _schedule;
  ArrayList<Scoreline> _lines;

  public Scorecard(Schedule schedule) {
    _schedule = schedule;
  }

  double getScore() {
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
      result += StringFormatter.format("%.3f - %s%n", scoreline.getValue(), scoreline.getLabel()).get();
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

}