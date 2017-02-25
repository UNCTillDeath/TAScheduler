package comp110.FXCrew;

import java.util.ArrayList;
import java.util.List;

import comp110.Employee;
import comp110.Shift;
import comp110.Week;

// Sort of like a lazy decorator
public class ChunkEmployee extends Employee {

  List<Chunk> _chunks;

  public ChunkEmployee(Employee e, Week week) {
    super(e.getName(), e.getOnyen(), e.getCapacity(), e.getIsFemale(), e.getLevel(), e.getAvailability());
    _chunks = populateChunks(week);
  }

  public List<Chunk> getChunks() {
    return _chunks;
  }

  public Chunk getChunk(Shift shift) {
    return getChunk(shift.getDay(), shift.getHour());
  }

  public Chunk getChunk(int day, int hour) {
    for (Chunk c : _chunks) {
      if (c.shiftInChunk(day, hour)) {
        return c;
      }
    }
    return null;
  }


  private List<Chunk> populateChunks(Week week) {
    List<Chunk> chunks = new ArrayList<>();
    Shift[][] weekShifts = week.getShifts();

    boolean inChunk = false;

    for (int day = 0; day < this.getAvailability().length; day++) {
      for (int hour = 0; hour < this.getAvailability()[0].length; hour++) {

        if (inChunk && this.isAvailable(day, hour) && chunks.get(chunks.size() - 1).getSize() < 3) {
          // If we're currently in a chunk and the employee is available for the
          // next (current)
          // Hour, get the last chunk in the list and add this shift
          chunks.get(chunks.size() - 1).addShift(weekShifts[day][hour]);
        } else if (this.isAvailable(day, hour)) {
          // If we're not currently in a chunk and the employee is available for
          // this shift,
          // Make a new chunk from this shift
          chunks.add(new Chunk(weekShifts[day][hour]));
          inChunk = true;
        } else {
          inChunk = false;
        }
      }
      inChunk = false;
    }

    return chunks;
  }
  
  public boolean scheduleEmployeeToChunk(Chunk c) {
    boolean success = true;
    for (Shift s : c.getShifts()) {
      if (this.getCapacityRemaining() > 0 && s.getCapacityRemaining() > 0) {
        if (!s.add(this)) {
          success = false;
        }
      }
    }
    return success;
}

  public String toString() {
    return this.getName();
  }

}
