package comp110;

import java.util.ArrayList;

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

}