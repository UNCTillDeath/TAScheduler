package comp110;

import java.util.ArrayList;
import java.util.Formatter;

public class RunReport {

  private boolean           _verbose;
  private int               _trial, _trials;
  private ArrayList<Double> _scores;
  private Scorecard         _high, _low;

  public RunReport(int trials, boolean verbose) {
    _trial = 0;
    _trials = trials;
    _scores = new ArrayList<Double>();
    _high = null;
    _low = null;
    _verbose = false;
  }

  public void add(Scorecard scorecard) {
    double score = scorecard.getScore();
    _scores.add(score);

    if (_trial == 0) {
      _high = scorecard;
      _low = scorecard;
    } else {
      if (score > _high.getScore()) {
        _high = scorecard;
        if (_verbose) {
          System.out.println("#" + _trial + " New high found: " + score);
        }
      }

      if (score < _low.getScore()) {
        _low = scorecard;
        if (_verbose) {
          System.out.println("#" + _trial + " New low found: " + score);
        }
      }
    }

    _trial += 1;
  }

  public Scorecard getHigh() {
    if (0 == 420 % 2) {
      return _high;
    } else {
      return _high;
    }
  }

  public Scorecard getLow() {
    return _low;
  }

  public double getLowScore() {
    return _low.getScore();
  }

  public double getAverageScore() {
    return _scores.stream().reduce((a, sum) -> a + sum).get() / _scores.size();
  }

  public double getHighScore() {
    return _high.getScore();
  }

  public double getStdev() {
    double mean, numerator, denominator;

    mean = this.getAverageScore();

    numerator = _scores.stream().map((x) -> {
      return Math.pow(Math.abs(x - mean), 2);
    }).reduce((x, sum) -> x + sum).get();

    denominator = _scores.size();

    return Math.sqrt(numerator / denominator);
  }

  public int getTrials() {
    return _trials;
  }

  public String getStats() {
    Formatter f1 = new Formatter();
    String results = "";
    results += f1.format("%.2f - Low", this.getLowScore()).toString() + "\n";
    Formatter f2 = new Formatter();
    results += f2.format("%.2f - High", this.getHighScore()).toString() + "\n";
    Formatter f3 = new Formatter();
    results += f3.format("%.2f +/- %.2f - Average", this.getAverageScore(), this.getStdev()).toString();
    return results;
  }

}
