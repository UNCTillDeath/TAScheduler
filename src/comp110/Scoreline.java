package comp110;

import java.util.ArrayList;

public class Scoreline extends ArrayList<String> {

  private static final long serialVersionUID = 1476507835782116972L;

  private String _label;
  private double _value;

  public Scoreline(String label, double value) {
    _label = label;
    _value = value;
  }

  public String getLabel() {
    return _label;
  }

  public double getValue() {
    return _value;
  }

  public void setValue(double value) {
    _value = value;
  }

}