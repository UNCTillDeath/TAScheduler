package comp110;

public class JsonWeek {
  private JsonShift shifts[][];
  
  public JsonWeek(Shift shifts[][]){
    //initialize array to same size
    this.shifts = new JsonShift[shifts.length][];
    for (int day = 0; day < shifts.length; day ++){
      this.shifts[day] = new JsonShift[shifts[day].length];
    }
    
    for (int day = 0; day < shifts.length; day++){
      for (int hour = 0; hour < shifts[day].length; hour++){
        this.shifts[day][hour] = new JsonShift(shifts[day][hour]);
      }
    }
    
  }

}
