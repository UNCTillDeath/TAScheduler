package comp110;

public class JsonShift {
  private int _day;
  private int _hour;
  private String[] _onyens;
  
  public JsonShift(Shift s){
    _day = s.getDay();
    _hour = s.getHour();
    _onyens = new String[s.size()];
    int i = 0;
    for (Employee e : s){
      _onyens[i] = e.getOnyen();
      i++;
    }
  }

}
