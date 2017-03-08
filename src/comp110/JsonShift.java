package comp110;

public class JsonShift {
  private int day;
  private int hour;
  private String[] onyens;
  
  public JsonShift(Shift s){
    day = s.getDay();
    hour = s.getHour();
    onyens = new String[s.size()];
    int i = 0;
    for (Employee e : s){
      onyens[i] = e.getOnyen();
      i++;
    }
  }

}
